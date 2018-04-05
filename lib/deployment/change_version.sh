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
  $debug mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -f "$1" -DnewVersion="$2"   -DartifactId=*  -DgroupId=*
}

function main {
	if [[ $# -eq 0 || "$1" == "-h" || "$1" == "--help"  ]]; then
	    helpAndExit 0
	fi	 
	if [[ "$TRAVIS_TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
		while [[ $# -gt 0 ]]; do
			change_version "$1" "${TRAVIS_TAG##v}"
			shift
		done
	else 
		echo "skipped version change because this is not a release build"
	fi
}

main "$@"
