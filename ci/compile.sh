#!/bin/bash
set -e # fail fast

#H Usage:
#H compile.sh -h | test.sh --help
#H
#H prints this help and exits
#H
#H compile.sh <module>
#H
#H   compiles the taskana application. Does not package and install artifacts.
#H
#H module:
#H   - WEB
#H   - LIB
#H   - REST
# Arguments:
#   $1: exit code
function helpAndExit() {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

function main() {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  REL=$(dirname "$0")
  case "$1" in
  WEB)
    (cd $REL/../web && npm install --silent)
    (cd $REL/../web && npm run build)
    ;;
  LIB)
    mvn -q install -N -Dcheckstyle.skip -f $REL/..
    mvn -q compile -f $REL/../lib
    ;;
  REST)
    mvn -q install -N -Dcheckstyle.skip
    mvn -q install -f lib -N -Dcheckstyle.skip
    mvn -q install -f lib/taskana-core -DskipTests -Dmaven.javadoc.skip
    mvn -q install -f lib/taskana-spring -DskipTests -Dmaven.javadoc.skip
    ;;
  esac
}

main "$@"
