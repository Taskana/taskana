#!/bin/bash
set -e #fail fast

#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE% [-i]
#H
#H   if a release version exists (extracted from GITHUB_REF environment variable)
#H   the taskana dependency in our wildfly example project will be incremented to the new version.
#H
#H i:
#H   increments version to next snapshot
# Arguments:
#   $1: exit code
function helpAndExit() {
  grep "^#H" "$0" | cut -c4- | sed -e "s/%FILE%/$(basename "$0")/g"
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
  echo "${1%\.*}.$(("${1##*\.*\.}" + 1))"
}

function main() {
  [[ "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  if [[ "$GITHUB_REF" =~ ^refs/tags/v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    REL=$(dirname "$0")
    while [[ $# -gt 0 ]]; do
      case $1 in
      -i)
        INCREMENT="true"
        shift # passed argument
        ;;
      *) # unknown option
        echo "unknown parameter $1" >&2
        exit 1
        ;;
      esac
    done
    FILES=(
      "$REL/../rest/taskana-rest-spring-example-wildfly/src/test/java/pro/taskana/example/wildfly/AbstractAccTest.java"
    )
    version=$([[ -n "$INCREMENT" ]] && echo "$(increment_version "${GITHUB_REF##refs/tags/v}")-SNAPSHOT" || echo "${GITHUB_REF##refs/tags/v}")
    for file in "${FILES[@]}"; do
      sed -i "s/\"[0-9]\+\.[0-9]\+\.[0-9]\+\(-SNAPSHOT\)\?\"/\"${version}\"/g" "$file"
    done
  else
    echo "skipped version change for wildfly because this is not a release build"
  fi
}

main "$@"
