#!/bin/bash
set -e #fail fast

#H Usage:
#H change_version.sh -h | change_version.sh --help
#H
#H prints this help and exits  
#H 
#H change_version.sh [modules...]
#H
#H   if a release version exists (extracted from TRAVIS_TAG) 
#H   the maven versions of all modules will be changed to the given release version.
#H
#H Environment variables:
#H   - TRAVIS_TAG 
#H       if this is a tagged build then TRAVIS_TAG contains the version number.
#H       pattern: v[DIGIT].[DIGIT].[DIGIT]
# Arguments:
#   $1: exitcode
function helpAndExit {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

# changing version in pom and all its children
# Arguments:
#   $1: directory of pom
#   $2: new version
function change_version {
  mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -f "$1" -DnewVersion="$2"   -DartifactId=*  -DgroupId=*
}

function main {
  if [[ $# -eq 0 || "$1" == "-h" || "$1" == "--help"  ]]; then
    helpAndExit 0
  fi

  while [[ $# -gt 0 ]]; do
    case $1 in
      -m|--modules)
        if [[ -z "$2" || "$2" == -* ]]; then
          echo "missing parameter for argument '-m|--modules'" >&2
          exit 1
        fi
        MODULES=($2)
        shift # past argument
        shift # past value
        ;;
      -swarm)
        if [[ -z "$2" || "$2" == -* ]]; then
          echo "missing parameter for argument '-swarm'" >&2
          exit 1
        fi
        SWARM="$2"
        shift # past argument
        shift # past value
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

  if [[ "$TRAVIS_TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    for dir in ${MODULES[@]}; do
      change_version "$dir" "${TRAVIS_TAG##v}"
    done

    if [[ -n "$SWARM" ]]; then
      sed -i "s/pro.taskana:taskana-core.*-SNAPSHOT/pro.taskana:taskana-core:${TRAVIS_TAG##v}/" "$SWARM"
    fi
  else
    echo "skipped version change because this is not a release build"
  fi
}

main "$@"
