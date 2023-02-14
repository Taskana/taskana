#!/bin/bash
set -e #fail fast

#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE% <m modules...> [-i]
#H
#H   if a release version exists (extracted from GITHUB_REF)
#H   the maven versions of all modules will be changed to the given release version.
#H
#H module:
#H   directory of a maven project
#H i:
#H   increments version
#H
#H Environment variables:
#H   - GITHUB_REF
#H       if this is a tagged build then GITHUB_REF contains the version number.
#H       pattern: refs/tags/v[DIGIT].[DIGIT].[DIGIT]
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

# changing version in pom and all its children
# Arguments:
#   $1: directory of pom
#   $2: new version
# Environment variable:
#   REL: relative path to this file
function change_version() {
  "$REL/../mvnw" -q versions:set -f "$1" -DnewVersion="$2" -DartifactId=* -DgroupId=* versions:commit
}

function main() {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  REL=$(dirname "$0")
  while [[ $# -gt 0 ]]; do
    case $1 in
    -i)
      INCREMENT="true"
      shift # passed argument
      ;;
    -m | --modules)
      if [[ -z "$2" || "$2" == -* ]]; then
        echo "missing parameter for argument '-m|--modules'" >&2
        exit 1
      fi
      MODULES=("$2")
      shift # passed argument
      shift # passed value
      ;;
    *) # unknown option
      echo "unknown parameter $1" >&2
      exit 1
      ;;
    esac
  done

  if [[ ${#MODULES[@]} -eq 0 ]]; then
    echo "Can not perform deployment without any modules" >&2
    helpAndExit 1
  fi

  if [[ "$GITHUB_REF" =~ ^refs/tags/v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    version=$([[ -n "$INCREMENT" ]] && echo "$(increment_version "${GITHUB_REF##refs/tags/v}")-SNAPSHOT" || echo "${GITHUB_REF##refs/tags/v}")
    for dir in "${MODULES[@]}"; do
      change_version "$dir" "$version"
    done
  else
    echo "skipped version change because this is not a release build"
  fi
}

main "$@"
