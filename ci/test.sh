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
  if [[ "$1" == "H2" && "$2" == "REST" ]]; then
    (cd $REL/../web && npm run test)
    (cd $REL/../rest/ && mvn clean install -q -B) #reinstalling rest because rest-doc is built during tests.
    (cd $REL/../rest/ && mvn clean verify -q -B -pl taskana-rest-spring-example -P history.plugin)
  elif [[ "$1" == "H2" && "$2" == "LIB" ]]; then
    (cd $REL/.. && mvn clean install -q -N -B )
    (cd $REL/../lib/ && mvn clean install -q -B -Dmaven.javadoc.skip)
  elif [[ "$1" == "POSTGRES_10_4" && "$2" == "CORE" ]]; then
    (cd $REL/.. && mvn clean install -q -N -B)
    (cd $REL/../lib && mvn clean install -q -N -B)
    (cd $REL/../lib/taskana-core && mvn clean verify -q -B)
  elif [[ "$1" == "POSTGRES_10_4" && "$2" == "WILDFLY" ]]; then
    #installing dependencies for rest (since this tests runs in a different cache)
    mvn clean install -q -N
    (cd $REL/../lib/ && mvn clean install -q -B -DskipTests -Dmaven.javadoc.skip)
    
    (cd $REL/../rest/ && mvn clean install -q -B -DskipTests -pl !taskana-rest-spring-wildfly-example -Dmaven.javadoc.skip)
    (cd $REL/../rest/ && mvn clean install -q -B -pl taskana-rest-spring-wildfly-example -Dmaven.javadoc.skip -P postgres)
  else
    (cd $REL/../lib/taskana-core && mvn clean verify -q -B)
  fi
}

main "$@"
