#!/bin/bash
set -e # fail fast

#H Usage:
#H test.sh -h | test.sh --help
#H
#H prints this help and exits
#H
#H test.sh <database>
#H
#H   tests the taskana application
#H
#H database:
#H   - H2
#H   - DB2_10_5
#H   - DB2_11_1
#H   - POSTGRES_10_4
# Arguments:
#   $1: exit code
function helpAndExit {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

function main {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  REL=`dirname "$0"`
  eval "$REL/prepare_db.sh '$1'"
  if [[ "$1" == "H2" ]]; then
    (cd $REL/../web && npm run test)
    mvn clean verify -q -f $REL/../lib/ -B
    mvn clean install -q -f $REL/../rest/ -B
  else
    mvn clean verify -q -f $REL/../lib/taskana-core -B
  fi
}

main "$@"
