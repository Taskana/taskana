#!/bin/bash
set -e # fail fast

#H Usage:
#H test.sh -h | test.sh --help
#H
#H prints this help and exits
#H
#H test.sh <database> <project>
#H
#H   tests the taskana application
#H
#H database:
#H   - H2
#H   - DB2_10_5
#H   - DB2_11_1
#H   - POSTGRES_10_4
#H project:
#H   - REST
#H   - WILDFLY
#H   - CORE
#H   - LIB
# Arguments:
#   $1: exit code
function helpAndExit {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

function main {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  REL=`dirname "$0"`
  eval "$REL/prepare_db.sh '$1'"
  #main stage H2 && REST it requires to package rest again because documentation is included in test phase
  if [[ "$1" == "H2" && "$2" == "REST" ]]; then
    (cd $REL/../web && npm run test)
    mvn clean install -q -f $REL/../rest/ -B
    mvn clean verify -q -f $REL/../rest/ -B -pl taskana-rest-spring-example -P history.plugin
  elif [[ "$1" == "H2" && "$2" == "LIB" ]]; then
    mvn clean install -q -f $REL/../lib/ -B -Dmaven.javadoc.skip
  elif [[ "$1" == "POSTGRES_10_4" && "$2" == "CORE" ]]; then
    mvn clean verify -q -f $REL/../lib/taskana-core -B
  elif [[ "$1" == "POSTGRES_10_4" && "$2" == "WILDFLY" ]]; then
    mvn clean install -q -f $REL/../lib/ -B -DskipTests -Dmaven.javadoc.skip
    mvn clean install -q -f $REL/../rest/ -B -DskipTests -pl !taskana-rest-spring-wildfly-example -Dmaven.javadoc.skip
    mvn clean install -q -f $REL/../rest/ -B -pl taskana-rest-spring-wildfly-example -Dmaven.javadoc.skip -P postgres
  else
    mvn clean verify -q -f $REL/../lib/taskana-core -B
  fi
}

main "$@"
