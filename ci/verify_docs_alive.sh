#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status
set -x # Print each command before executing it

BASE_URL=https://taskana.azurewebsites.net/taskana
TIMEOUT=30 # Timeout duration in seconds
RETRIES=3 # Number of retries
DELAY=5 # Delay in seconds between retries

check_url() {
  local url=$1
  local expected_status=$2
  local attempt=1
  local status_code

  while [ $attempt -le $RETRIES ]; do
    status_code=$(curl -sw "%{http_code}" -o /dev/null --connect-timeout $TIMEOUT --max-time $TIMEOUT "$url")
    if [ "$status_code" -eq "$expected_status" ]; then
      echo "URL $url is accessible with status $status_code"
      return 0
    else
      echo "Attempt $attempt: URL $url returned status $status_code, expected $expected_status"
      if [ $attempt -lt $RETRIES ]; then
        echo "Retrying in $DELAY seconds..."
        sleep $DELAY
      fi
    fi
    attempt=$((attempt + 1))
  done

  echo "Error: URL $url did not return expected status $expected_status after $RETRIES attempts"
  return 1
}

check_url "$BASE_URL/docs/rest/rest-api.html" 200
check_url "$BASE_URL/docs/rest/simplehistory-rest-api.html" 200
check_url "$BASE_URL/docs/rest/routing-rest-api.html" 200

for module in taskana-core taskana-spring; do
  check_url "$BASE_URL/docs/java/$module/index.html" 200
done

for module in taskana-cdi; do
  check_url "$BASE_URL/docs/java/$module/pro/taskana/common/internal/package-summary.html" 200
done
