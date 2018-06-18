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
#H   - DB2_10_5
#H   - DB2_11_1
#H   - POSTGRES
# Arguments:
#   $1: exit code
function helpAndExit {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

function main {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  propFile="$HOME/taskanaUnitTest.properties" 
   
  case "$1" in 
    H2)
      ;;
    DB2_10_5)
      if [[ -z `docker ps -aq -f name=^/taskana-db2_10_5$ -f status=running` ]]; then
        if [[ -z `docker ps -aq -f name=^/taskana-db2_10_5$` ]]; then
          docker run -d -p 49999:50000 --name taskana-db2_10_5 mustaphazorgati/db2:10.5 -d
        else
          docker start taskana-db2_10_5
        fi
      fi
      echo 'jdbcDriver=com.ibm.db2.jcc.DB2Driver' > $propFile
      echo 'jdbcUrl=jdbc:db2://localhost:49999/tskdb' >> $propFile
      echo 'dbUserName=db2inst1' >> $propFile
      echo 'dbPassword=db2inst1-pwd' >> $propFile
      ;;
    DB2_11_1)
      if [[ -z `docker ps -aq -f name=^/taskana-db2_11_1$ -f status=running` ]]; then
        if [[ -z `docker ps -aq -f name=^/taskana-db2_11_1$` ]]; then
          docker run -d -p 50000:50000 --name taskana-db2_11_1 mustaphazorgati/db2:11.1 -d
        else
          docker start taskana-db2_11_1
        fi
      fi
      echo 'jdbcDriver=com.ibm.db2.jcc.DB2Driver' > $propFile
      echo 'jdbcUrl=jdbc:db2://localhost:50000/tskdb' >> $propFile
      echo 'dbUserName=db2inst1' >> $propFile
      echo 'dbPassword=db2inst1-pwd' >> $propFile
      ;;
    POSTGRES)
      if [[ -z `docker ps -aq -f name=^/taskana-postgres$ -f status=running` ]]; then
        if [[ -z `docker ps -aq -f name=^/taskana-postgres$` ]]; then
          docker run -d -p 50001:5432 --name taskana-postgres -e POSTGRES_PASSWORD=postgres postgres:10.4
        else
          docker start taskana-postgres
        fi
      fi
      echo 'jdbcDriver=org.postgresql.Driver' > $propFile
      echo 'jdbcUrl=jdbc:postgresql://localhost:50001/postgres' >> $propFile
      echo 'dbUserName=postgres' >> $propFile   
      echo 'dbPassword=postgres' >> $propFile
      ;;
    *)
      echo "unknown database '$1'" >&2
      exit 1
  esac

  docker ps
}

main "$@"
