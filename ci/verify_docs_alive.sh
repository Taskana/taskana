#!/bin/bash
set -e # fail fast
set -x
BASE_URL=https://taskana.azurewebsites.net/taskana

test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/api-docs")"
test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/swagger-ui/index.html")"
for module in taskana-core taskana-spring; do
  test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/java/$module/index.html")"
done
for module in taskana-cdi; do
  test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/java/$module/pro/taskana/common/internal/package-summary.html")"
done
