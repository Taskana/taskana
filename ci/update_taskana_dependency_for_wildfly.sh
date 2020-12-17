#!/bin/bash
set -e #fail fast

#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE%
#H
#H   if a release version exists (extracted from TRAVIS_TAG environment variable)
#H   the taskana dependency in our wildfly example project will be incremented to the new version snapshot.
#H
# Arguments:
#   $1: exit code
function helpAndExit() {
  cat "$0" | grep "^#H" | cut -c4- | sed -e "s/%FILE%/$(basename "$0")/g"
  exit "$1"
}

# takes a version (without leading v) and increments its
# last number by one.
# Arguments:
#   $1: version (without leading v) which will be patched
# Return:
#   version with last number incremented
function increment_version() {
  if [[ ! "$1" =~ [0-9]+\.[0-9]+\.[0-9]+ ]]; then
    echo "'$1' does not match tag pattern." >&2
    exit 1
  fi
  echo "${1%\.*}.$(expr ${1##*\.*\.} + 1)"
}

function main() {
  [[ "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  if [[ "$TRAVIS_TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    REL=$(dirname "$0")
    FILES=(
      $REL/../rest/taskana-rest-spring-example-wildfly/pom.xml
    )
    for file in ${FILES[@]}; do
      sed -i "s/[0-9]\+\.[0-9]\+\.[0-9]\+-SNAPSHOT/$(increment_version "${TRAVIS_TAG##v}")-SNAPSHOT/g" $file
    done
  fi
}

main "$@"