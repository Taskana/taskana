#!/bin/bash
set -e #fail fast

#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE% <database>
#H
#H   downloads and starts docker image for taskana unit tests.
#H
#H %FILE% stop [database]
#H
#H   stops the database and removes 'taskanaUnitTest.properties'.
#H   If no database was provided all databases are stopped.
#H
#H database:
#H   - H2
#H   - DB2_11_1
#H   - POSTGRES_10
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
    DB2_11_1)
      echo "taskana-db2_11-1"
      ;;
    POSTGRES_10)
      echo "taskana-postgres_10"
      ;;
    *)
      echo "unknown database '$1'" >&2 && exit 1
  esac
}

function main() {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  propFile="$HOME/taskanaUnitTest.properties"
  scriptDir=$(dirname "$0")

  case "$1" in
  H2)
    [[ -f "$propFile" ]] && rm "$propFile"
    ;;
  DB2_11_1)
    docker-compose -f $scriptDir/docker-compose.yml up -d "$(mapDBToDockerComposeServiceName "$1")"

    echo 'jdbcDriver=com.ibm.db2.jcc.DB2Driver' > $propFile
    echo 'jdbcUrl=jdbc:db2://localhost:5101/tskdb' >> $propFile
    echo 'dbUserName=db2inst1' >> $propFile
    echo 'dbPassword=db2inst1-pwd' >> $propFile
    echo 'schemaName=TASKANA' >> $propFile
    ;;
  POSTGRES_10)
    docker-compose -f $scriptDir/docker-compose.yml up -d "$(mapDBToDockerComposeServiceName "$1")"
   
    echo 'jdbcDriver=org.postgresql.Driver' > $propFile
    echo 'jdbcUrl=jdbc:postgresql://localhost:5102/postgres' >> $propFile
    echo 'dbUserName=postgres' >> $propFile
    echo 'dbPassword=postgres' >> $propFile
    echo 'schemaName=taskana' >> $propFile
    ;;
  stop)
    docker-compose -f $scriptDir/docker-compose.yml rm -f -s -v "$(mapDBToDockerComposeServiceName "$2")"
    
    [[ -f "$propFile" ]] && rm "$propFile"
    ;;
  *)
    echo "unknown database '$1'" >&2
    exit 1
    ;;
  esac

  docker ps
}

main "$@"
