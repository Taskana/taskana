#!/bin/bash
set -e # fail fast

#H Usage:
#H test.sh -h | test.sh --help
#H
#H prints this help and exits
#H
#H test.sh <database|module>
#H
#H   tests the taskana application. See documentation for further testing details.
#H
#H database:
#H   - H2
#H   - DB2_10_5
#H   - DB2_11_1
#H   - POSTGRES_10_4
#H module:
#H   - HISTORY
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
  H2)
    set -x
    eval "$REL/prepare_db.sh '$1'"
    # We can not use the fance '-f' maven option due to a bug in arquillian. See https://issues.jboss.org/browse/THORN-2049
    (cd $REL/.. && mvn -q install -B -T 4C -am -Dmaven.javadoc.skip -Dcheckstyle.skip)
    ;;
  DB2_10_5 | DB2_11_1)
    set -x
    eval "$REL/prepare_db.sh '$1'"
    mvn -q verify -B -f $REL/.. -am -T 4C -Dmaven.javadoc.skip -Dcheckstyle.skip -pl :taskana-core
    ;;
  POSTGRES_10_4)
    set -x
    eval "$REL/prepare_db.sh '$1'"
    ### INSTALL ###
    mvn -q install -B -f $REL/.. -P postgres -am -T 4C -pl :taskana-rest-spring-wildfly-example -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip

    ### TEST ###
    mvn -q verify -B -f $REL/.. -Dmaven.javadoc.skip -Dcheckstyle.skip -pl :taskana-core
    # Same as above (H2) we can not use the fancy '-f' maven option
    (cd $REL/.. && mvn -q verify -B -pl :taskana-rest-spring-wildfly-example -P postgres -Dmaven.javadoc.skip -Dcheckstyle.skip)
    ;;
  HISTORY)
    set -x
    ### INSTALL ###
    mvn -q install -B -f $REL/.. -am -T 4C -pl :taskana-rest-spring -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip

    ### TEST ###
    mvn -q verify -B -f $REL/../history -Dmaven.javadoc.skip -Dcheckstyle.skip
    ;;
  WEB)
    set -x
    (cd $REL/../web && npm install --silent && npm run test)
    ;;
  esac
}

main "$@"
