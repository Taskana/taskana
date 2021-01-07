#!/bin/bash
set -e # fail fast
#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE% <module>
#H
#H   compiles the taskana application. Does not package and install artifacts.
#H
#H module:
#H   - WEB
#H   - COMMON
#H   - LIB
#H   - REST
#H   - HISTORY
# Arguments:
#   $1: exit code
function helpAndExit() {
  cat "$0" | grep "^#H" | cut -c4- | sed -e "s/%FILE%/$(basename "$0")/g"
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
  COMMON)
    set -x
    $REL/../mvnw -q install -B -T 2C -f $REL/.. -DskipTests -Dcheckstyle.skip -Dmaven.javadoc.skip -N
    $REL/../mvnw -q test-compile -B -T 2C -f $REL/../common
    ;;
  LIB)
    set -x
    $REL/../mvnw -q install -B -T 2C -f $REL/.. -pl :taskana-core -am -DskipTests -Dcheckstyle.skip -Dmaven.javadoc.skip
    $REL/../mvnw -q test-compile -B -T 2C -f $REL/../lib
    ;;
  REST)
    set -x
    $REL/../mvnw -q install -B -T 2C -f $REL/.. -pl :taskana-simplehistory-rest-spring -am -DskipTests -Dcheckstyle.skip -Dmaven.javadoc.skip -Dasciidoctor.skip
    $REL/../mvnw -q test-compile -B -T 2C -f $REL/../rest
    ;;
  HISTORY)
    set -x
    $REL/../mvnw -q install -B -T 2C -f $REL/.. -pl :taskana-rest-spring -am -DskipTests -Dcheckstyle.skip -Dmaven.javadoc.skip -Dasciidoctor.skip
    $REL/../mvnw -q test-compile -B -T 2C -f $REL/../history
    ;;
  esac
}

main "$@"
