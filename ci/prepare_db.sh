#!/bin/bash
set -e #fail fast

#H Usage:
#H prepare_db.sh -h | prepare_db.sh --help
#H
#H prints this help and exits
#H
#H prepare_db.sh <database>
#H
#H   downloads and starts docker image for taskana unit tests
#H
#H database:
#H   - H2
#H   - DB2_11_1
#H   - POSTGRES_10
# Arguments:
#   $1: exit code
function helpAndExit() {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

function main() {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  propFile="$HOME/taskanaUnitTest.properties"
  scriptDir=`dirname $0`

  case "$1" in
  H2)
    if [[ -f "$propFile" ]]; then
      rm "$propFile"
    fi
    ;;
  DB2_11_1)
    if [[ -z $(docker ps -aq -f name=^/taskana-db2_11_1$ -f status=running) ]]; then
      if [[ -z $(docker ps -aq -f name=^/taskana-db2_11_1$) ]]; then
        docker run -d -p 50101:50000 --name taskana-db2_11_1 taskana/db2:11.1 -d
      else
        docker start taskana-db2_11_1
      fi
    fi
    echo 'jdbcDriver=com.ibm.db2.jcc.DB2Driver' >$propFile
    echo 'jdbcUrl=jdbc:db2://localhost:50101/tskdb' >>$propFile
    echo 'dbUserName=db2inst1' >>$propFile
    echo 'dbPassword=db2inst1-pwd' >>$propFile
    echo 'schemaName=TASKANA' >>$propFile
    ;;
  POSTGRES_10)
    docker-compose -f $scriptDir/docker-compose.yml up -d
   
    echo 'jdbcDriver=org.postgresql.Driver' >$propFile
    echo 'jdbcUrl=jdbc:postgresql://localhost:50102/postgres' >>$propFile
    echo 'dbUserName=postgres' >>$propFile
    echo 'dbPassword=postgres' >>$propFile
    echo 'schemaName=taskana' >>$propFile
    ;;
  *)
    echo "unknown database '$1'" >&2
    exit 1
    ;;
  esac

  docker ps
}

main "$@"
