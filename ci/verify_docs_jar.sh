#!/bin/bash
set -e #fail fast

#check that first dir has same amount of files as second dir
function verifyDocs() {
  test -d "$1"
  test $(find "$1" | wc -l) -eq $(grep "$2" <<<"$JAR_CONTENT" | wc -l)
}

REL=$(dirname "$0")
JAR_FILE_LOCATION="$REL/../rest/taskana-rest-spring-example-boot/target/taskana-rest-spring-example-boot.jar"
set -x
test -e "$JAR_FILE_LOCATION"
set +x
JAR_CONTENT=$(jar tf "$JAR_FILE_LOCATION")
set -x

verifyDocs "$REL/../lib/taskana-core/target/apidocs" "/static/docs/java/taskana-core"
verifyDocs "$REL/../lib/taskana-cdi/target/apidocs" "/static/docs/java/taskana-cdi"
verifyDocs "$REL/../lib/taskana-spring/target/apidocs" "/static/docs/java/taskana-spring"
test -n "$(jar -tf $JAR_FILE_LOCATION | grep /static/docs/rest/rest-api.html)"
test -n "$(jar -tf $JAR_FILE_LOCATION | grep /static/docs/rest/simplehistory-rest-api.html)"
test -n "$(jar -tf $JAR_FILE_LOCATION | grep /static/docs/rest/routing-rest-api.html)"
set +x
echo "the jar file '$JAR_FILE_LOCATION' contains documentation"
