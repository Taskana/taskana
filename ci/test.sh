#!/bin/bash
set -e # fail fast

#H Usage:
#H test.sh -h | test.sh --help
#H
#H prints this help and exits
#H
#H test.sh <database>
#H
#H   tests the taskana application. See documentation for further testing details.
#H
#H database:
#H   - H2
#H   - DB2_10_5
#H   - DB2_11_1
#H   - POSTGRES_10_4
# Arguments:
#   $1: exit code
function helpAndExit() {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

function main() {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  REL=$(dirname "$0")
  eval "$REL/prepare_db.sh '$1'"
  case "$1" in
  H2)
    (cd $REL/.. && mvn -q install -B -T 4C -am -Dmaven.javadoc.skip -Dcheckstyle.skip)
    (cd $REL/../web && npm install --silent && npm run test)
    ;;
  DB2_10_5 | DB2_11_1)
    (cd $REL/.. && mvn -q verify -B -am -Dmaven.javadoc.skip -Dcheckstyle.skip -pl :taskana-core)
    ;;
  POSTGRES_10_4)
    ### INSTALL ###
    (cd $REL/.. && mvn -q install -B -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip -P postgres -am -T 4C -pl :taskana-rest-spring-wildfly-example)

    ### TEST ###
    (cd $REL/.. && mvn -q verify -B -Dmaven.javadoc.skip -Dcheckstyle.skip -pl :taskana-core)
    (cd $REL/../rest/taskana-rest-spring-wildfly-example && mvn -q verify -B -P postgres -Dmaven.javadoc.skip -Dcheckstyle.skip)
    ;;
  esac
}

main "$@"
