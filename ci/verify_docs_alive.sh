#!/bin/bash
set -e # fail fast
set -x
BASE_URL=https://taskana.mybluemix.net/taskana

test 200 -eq $(curl -sw %{http_code} -o /dev/null "$BASE_URL/docs/rest/rest-api.html")
test -z "$(curl -s $BASE_URL/docs/rest/rest-api.html | grep 'Unresolved directive.*adoc')"
for module in taskana-core taskana-spring; do
  test 200 -eq $(curl -sw %{http_code} -o /dev/null "$BASE_URL/docs/java/$module/pro/taskana/package-summary.html")
done
for module in taskana-cdi; do
  test 200 -eq $(curl -sw %{http_code} -o /dev/null "$BASE_URL/docs/java/$module/pro/taskana/common/internal/package-summary.html")
done
