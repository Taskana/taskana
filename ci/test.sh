#!/bin/bash
set -e # fail fast

#H Usage:
#H test.sh -h | test.sh --help
#H
#H prints this help and exits
#H
#H test.sh <database|module> [sonar project key]
#H
#H   tests the taskana application. See documentation for further testing details.
#H
#H database:
#H   - H2
#H   - DB2_11_1
#H   - POSTGRES_10
#H module:
#H   - HISTORY
#H sonar project key:
#H   the key of the sonarqube project where the coverage will be sent to.
#H   If empty nothing will be sent
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
    (cd $REL/.. && mvn -q install -B -T 4C -am -Pcoverage -Dcheckstyle.skip)
    eval "$REL/verify_docs_jar.sh"
    # disabling sonarqube for PRs because it's not supported yet. See https://jira.sonarsource.com/browse/MMF-1371
    if [ -n "$2" ]; then
      #-Pcoverage to activate jacoco and test coverage reports
      # send test coverage and build information to sonarcloud
      mvn sonar:sonar -f $REL/.. -Pcoverage -Dsonar.projectKey="$2"
    fi
    ;;
  DB2_11_1)
    set -x
    eval "$REL/prepare_db.sh '$1'"
    mvn -q verify -B -f $REL/.. -am -T 4C -Dmaven.javadoc.skip -Dcheckstyle.skip -pl :taskana-core
    ;;
  POSTGRES_10)
    set -x
    eval "$REL/prepare_db.sh '$1'"
    ### INSTALL ###
    mvn -q install -B -f $REL/.. -P postgres -am -T 4C -pl :taskana-rest-spring-example-wildfly -Dasciidoctor.skip -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip

    ### TEST ###
    mvn -q verify -B -f $REL/.. -Dmaven.javadoc.skip -Dcheckstyle.skip -pl :taskana-core
    # Same as above (H2) we can not use the fancy '-f' maven option
    (cd $REL/.. && mvn -q verify -B -pl :taskana-rest-spring-example-wildfly -Ddb.type=postgres -Dmaven.javadoc.skip -Dcheckstyle.skip)
    ;;
  HISTORY)
    set -x
    ### INSTALL ###
    mvn -q install -B -f $REL/.. -am -T 4C -pl :taskana-rest-spring -Dasciidoctor.skip -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip

    ### TEST ###
    mvn -q verify -B -f $REL/../history -Dmaven.javadoc.skip -Dcheckstyle.skip
    ;;
  WEB)
    set -x
    ### INSTALL ###

    (cd $REL/../web && npm install --silent && npm run build:prod-silent)
    mvn -q install -B -f $REL/.. -am -T 4C -pl :taskana-rest-spring-example-boot -Dasciidoctor.skip -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip -P history.plugin
    mvn spring-boot:run -P history.plugin -f $REL/../rest/taskana-rest-spring-example-boot > /dev/null &

    ### TEST ###
    (cd $REL/../web && npm run test -- --coverageReporters text-summary)
    ### TEMP REMOVE CYPRESS TESTS ###
    ### (cd $REL/../web && npm run e2e -- --config-file ../ci/cypress.json) ###

    ### CLEANUP ###
    jobs -p | xargs -rn10 kill
    ;;
  esac
}
main "$@"
