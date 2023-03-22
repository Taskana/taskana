#!/bin/bash
set -e #fail fast
trap "exit 1" TERM
export TOP_PID=$$

#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE% <database>
#H
#H   downloads and starts docker image for TASKANA unit tests.
#H
#H %FILE% stop [database]
#H
#H   stops the database.
#H   If no database was provided all databases are stopped.
#H
#H database:
#H   - DB2 | DB2_11_5
#H   - POSTGRES | POSTGRES_14
#H   - ORACLE | ORACLE_18
# Arguments:
#   $1: exit code
function helpAndExit() {
  cat "$0" | grep "^#H" | cut -c4- | sed -e "s/%FILE%/$(basename "$0")/g"
  exit "$1"
}

# This function maps the database parameter (for this file) to the docker-compose service name.
# Arguments:
#   $1: the database which should be mapped
function mapDBToDockerComposeServiceName() {
  [[ -z "$1" || "$1" == "H2" ]] && return
  case "$1" in
    DB2|DB2_11_5)
      echo "taskana-db2_11-5"
      ;;
    POSTGRES|POSTGRES_14)
      echo "taskana-postgres_14"
      ;;
    ORACLE|ORACLE_18)
      echo "taskana-oracle-18"
      ;;
    *)
      echo "unknown database '$1'" >&2 && kill -s TERM $TOP_PID
  esac
}

function main() {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  scriptDir=$(dirname "$0")

  case "$1" in
  H2)
    ;;
  DB2|DB2_11_5)
    docker-compose -f $scriptDir/docker-compose.yml up -d "$(mapDBToDockerComposeServiceName "$1")"
    ;;
  POSTGRES|POSTGRES_14)
    docker-compose -f $scriptDir/docker-compose.yml up -d "$(mapDBToDockerComposeServiceName "$1")"
    ;;
  ORACLE|ORACLE_18)
    docker-compose -f $scriptDir/docker-compose.yml up -d "$(mapDBToDockerComposeServiceName "$1")"
    ;;
  stop)
    # this variable is necessary, so that the script can terminate properly
    # when the provided database name does not match. PLEASE DO NOT INLINE!
    local composeServiceName="$(mapDBToDockerComposeServiceName "$2")"
    docker-compose -f $scriptDir/docker-compose.yml rm -f -s -v $composeServiceName
    ;;
  *)
    echo "unknown database '$1'" >&2
    exit 1
    ;;
  esac

  docker ps
}

main "$@"
