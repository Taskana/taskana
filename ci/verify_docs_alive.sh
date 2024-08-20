#!/bin/bash
set -e # fail fast
set -x
BASE_URL=https://kadai.azurewebsites.net/kadai

test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/api-docs")"
test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/swagger-ui/index.html")"
for module in kadai-core kadai-spring; do
  test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/java/$module/index.html")"
done
for module in kadai-cdi; do
  test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/java/$module/io/kadai/common/internal/package-summary.html")"
done
