(ns mw-cli.core
  "Making MicroWorld a command line executable tool"
  (:require [clojure.java.browse :refer [browse-url]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :refer [join]]
            [clojure.tools.cli :refer [parse-opts]]
            [environ.core :refer [env]]
            [hiccup.core :refer [html]]
            [me.raynes.fs :refer [parent writeable?]]
            [mw-engine.core :refer [run-world]]
            [mw-engine.display :refer [*image-base* render-world-table]]
            [mw-engine.heightmap :refer [apply-heightmap]]
            [mw-parser.bulk :refer [compile-file]]
            [taoensso.timbre :as l])
  (:gen-class))


(def cli-options
  "Command-line interface options"
  [["-d" "--display FILE" "Display generated world as HTML file"]
   ["-g" "--generations GENS" "The number of generations to run."
    :required true
    :parse-fn #(Integer/parseInt %)]
   ["-h" "--help" "Show this message"
    :default false]
   ["-m" "--heightmap FILE" "The path to the file containing the heightmap to load."
    :parse-fn #(apply-heightmap %)]
   ["-o" "--output FILE" "The path to the EDN file to which to output the result."
    :validate [#(writeable? (parent %)) "Must be writeable"]]
   ["-r" "--rules FILE" "The path to the file containing the rules to run."
    :required true
    :parse-fn #(compile-file %)]
   ["-v" "--verbosity [LEVEL]" nil "Verbosity level - integer value required."
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 5) "Must be a number between 1 and 4."]
    :default 0]])


(defn print-usage
  "Print a UN*X style usage message. `project-name` should be the base name of
  the executable jar file you generate, `parsed-options` should be options as
  parsed by [clojure.tools.cli](https://github.com/clojure/tools.cli). If
  `extra-args` is supplied, it should be a map of name, documentation pairs
  for each additional argument which may be supplied."
  ([project-name parsed-options]
   (print-usage project-name parsed-options {}))
  ([project-name parsed-options extra-args]
   (println
     (join
       "\n"
       (flatten
         (list
           (join " "
                 (concat
                   (list
                     "Usage: java -jar "
                     (str
                       project-name
                       "-"
                       (or (System/getProperty (str project-name ".version"))
                           (env (keyword (str project-name "-version")))
                           "[VERSION]")
                       "-standalone.jar")
                     "-options")
                   (map name (keys extra-args))))
           "where options include:"
           (:summary parsed-options)
           (doall
             (map
               #(str "  " (name %) "\t\t" (extra-args %))
               (keys extra-args)))))))))


(defn usage
  "Show a usage message. `parsed-options` should be options as
  parsed by [clojure.tools.cli](https://github.com/clojure/tools.cli)"
  [parsed-options]
  (print-usage
    "mw-cli"
    parsed-options))


(defn process
  ([options]
   (let [o (:options options)]
     (process (:heightmap o) (:rules o) (:generations o) (:output o) (:display o))))
  ([initial-world rules generations output-name display-name]
   (l/info "Processing for" generations "iterations to" output-name)
   (let [world (run-world initial-world nil rules generations)]
     (l/info "Processing complete; writing EDN to " (or output-name "standard out"))
     (with-open [scrivener (if output-name (clojure.java.io/writer output-name) *out*)]
       (binding [*out* scrivener]
         (pr world)))
     (when display-name
       (l/info "Writing HTML rendering to " display-name)
       (with-open [scrivener (clojure.java.io/writer display-name)]
         (do
           (binding [*out* scrivener]
             (spit
               display-name
               (html [:html
                      [:head
                       [:title "Microworld render"]
                       [:link {:href "https://www.journeyman.cc/mw-ui-assets/css/states.css" :rel "stylesheet" :type "text/css"}]]
                      [:body
                       (binding [*image-base* "https://www.journeyman.cc/mw-ui-assets/img/tiles"]
                         (render-world-table world))]])))
           (browse-url display-name))))
     world)))

;; (def rules (compile-file "../mw-ui/resources/public/rulesets/settlement.txt"))
;; (def heightmap (apply-heightmap "../mw-ui/resources/public/img/heightmaps/small_hill.png"))

;; (process heightmap rules 1 "small_hill.edn" "small_hill.html")

(defn -main
  "Parses options and arguments. Expects as args the path-name of one or
  more ADL files."
  [& args]
  (let [options (parse-opts args cli-options)
        logging-levels [:warn :info :debug :trace]
        verbosity (:verbosity (:options options))]
    (when verbosity
      (l/set-level!
        (nth
          logging-levels
          (mod
            (dec verbosity)
            (count logging-levels)))))
    (cond
      (empty? args)
      (usage options)
      (seq (:errors options))
      (do
        (doall
          (map
            println
            (:errors options)))
        (usage options)
        (System/exit 1))
      (-> options :options :help)
      (usage options)
      true
      (process options))))
