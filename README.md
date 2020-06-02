# mw-cli

Making MicroWorld a command line executable tool. Essentially, a mechanism to run a simulation for fixed number of generations, and then dump the result into an edn file.

For more information, see
[mw-parser](https://github.com/simon-brooke/mw-parser) and
[mw-engine](https://github.com/simon-brooke/mw-engine).

## Installation

Download from http://example.com/FIXME.

## Usage

    $ java -jar mw-cli-0.1.0-standalone.jar [options]

where options include:

* `-d, --display FILE`          Display generated world as HTML file
* `-g, --generations true`      The number of generations to run.
* `-h, --help`                  Show this message
* `-m, --heightmap FILE`        The path to the raster (gif, jpeg or png) file containing the heightmap to load.
* `-o, --output FILE`           The path to the EDN file to which to output the result.
* `-r, --rules true`            The path to the text file containing the rules to run.
* `-v, --verbosity [LEVEL]`     The amount of logging information to output; an integer between 1 and 4.

## Examples

    java -jar mw-cli-0.1.6-SNAPSHOT-standalone.jar \
        --heightmap isle_of_man.png --output isle_of_man.edn \
        --rules settlement.txt -g100 -v4 \
        --display isle_of_man.html

### Bugs

Probably.

## License

Copyright © 2020 [Simon Brooke](mailto:simon@journeyman.cc)

Distributed under the terms of the
[GNU General Public License v2](http://www.gnu.org/licenses/gpl-2.0.html)
