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
#H   - WILDFLY
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
    # We can not use the fancy '-f' maven option due to a bug in arquillian. See https://issues.jboss.org/browse/THORN-2049
    (cd $REL/.. && ./mvnw -q install -B -T 2C -Pcoverage -Dcheckstyle.skip)
    eval "$REL/verify_docs_jar.sh"
    # disabling sonarqube for PRs because it's not supported yet. See https://jira.sonarsource.com/browse/MMF-1371
    if [ -n "$2" ]; then
      # -Pcoverage to activate jacoco and test coverage reports
      # send test coverage and build information to sonarcloud
      $REL/../mvnw -q sonar:sonar -B -T 2C -f $REL/.. -Pcoverage -Dsonar.projectKey="$2"
    fi
    ;;
  DB2_11_1)
    set -x
    eval "$REL/prepare_db.sh '$1'"
    $REL/../mvnw -q verify -B -T 2C -f $REL/.. -pl :taskana-core -am -Dmaven.javadoc.skip -Dcheckstyle.skip
    ;;
  POSTGRES_10)
    set -x
    eval "$REL/prepare_db.sh '$1'"
    ### INSTALL ###
    $REL/../mvnw -q install -B -T 2C -f $REL/.. -pl :taskana-rest-spring-example-common -am -P postgres -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip -Dasciidoctor.skip

    ### TEST ###
    $REL/../mvnw -q verify -B -T 2C -f $REL/.. -pl :taskana-core -Dmaven.javadoc.skip -Dcheckstyle.skip
    ;;
  WILDFLY)
    set -x
    eval "$REL/prepare_db.sh 'POSTGRES_10'"
    # Same as above (H2) we can not use the fancy '-f' maven option
    (cd $REL/../rest/taskana-rest-spring-example-wildfly && ../../mvnw -q verify -B -T 2C -Ddb.type=postgres)
    ;;
  HISTORY)
    set -x
    ### INSTALL ###
    $REL/../mvnw -q install -B -T 2C -f $REL/.. -pl :taskana-rest-spring -am -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip -Dasciidoctor.skip

    ### TEST ###
    $REL/../mvnw -q verify -B -T 2C -f $REL/../history -Dmaven.javadoc.skip -Dcheckstyle.skip
    ;;
  WEB)
    set -x
    ### INSTALL ###

    (cd $REL/../web && npm install --silent && npm run build:prod-silent)
    $REL/../mvnw -q install -B -T 2C -f $REL/.. -pl :taskana-rest-spring-example-boot -am -P history.plugin -DskipTests -Dmaven.javadoc.skip -Dcheckstyle.skip -Dasciidoctor.skip
    $REL/../mvnw spring-boot:run -P history.plugin -f $REL/../rest/taskana-rest-spring-example-boot > /dev/null &

    ### TEST ###
    (cd $REL/../web && npm run test -- --coverageReporters text-summary)
    (cd $REL/../web && npm run e2e -- --config-file ../ci/cypress.json)

    ### CLEANUP ###
    jobs -p | xargs -rn10 kill
    ;;
  esac
}

main "$@"
