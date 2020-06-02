(defproject mw-cli "0.1.6-SNAPSHOT"
  :description "Command-line launcher for MicroWorld engine."
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License,version 2.0 or (at your option) any later version"
            :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.reader "1.3.2"]
                 [com.taoensso/timbre "4.10.0" :exclusions [org.clojure/tools.reader]]
                 [environ "1.2.0"]
                 [mw-engine "0.1.6-SNAPSHOT"]
                 [mw-parser "0.1.6-SNAPSHOT"]
                 [me.raynes/fs "1.4.6"]]
  :main ^:skip-aot mw-cli.core
  :target-path "target/%s"
  :plugins [[lein-environ "1.2.0"]]
  :profiles {:uberjar {:aot :all}})
