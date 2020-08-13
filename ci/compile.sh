#!/bin/bash
set -e # fail fast
#H Usage:
#H compile.sh -h | compile.sh --help
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
    set -x
    (cd $REL/../web && npm install --silent)
    (cd $REL/../web && npm run lint)
    (cd $REL/../web && npm run build)
    ;;
  LIB)
    set -x
    mvn -q install -B -f $REL/.. -DskipTests -Dcheckstyle.skip -Dmaven.javadoc.skip -N
    mvn -q test-compile -B -f $REL/../lib
    ;;
  REST)
    set -x
    mvn -q install -B -f $REL/.. -pl :taskana-simplehistory-rest-spring -am -Dasciidoctor.skip -DskipTests -Dcheckstyle.skip -Dmaven.javadoc.skip
    mvn -q test-compile -B -f $REL/../rest
    ;;
  HISTORY)
    set -x
    mvn -q install -B -f $REL/.. -pl :taskana-rest-spring -am -Dasciidoctor.skip -DskipTests -Dcheckstyle.skip -Dmaven.javadoc.skip
    mvn -q test-compile -B -f $REL/../history
    ;;
  esac
}

main "$@"
