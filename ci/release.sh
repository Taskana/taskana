#!/bin/bash
set -e # fail fast

#H Usage:
#H release.sh -h | release.sh --help
#H
#H prints this help and exits
#H 
#H release.sh <module> [version]
#H
#H   an easy deployment tool to deploy maven projects.
#H
#H module:
#H
#H   path to maven project
#H
#H version:
#H
#H   new version for the <module>.
#H   format: ^v[0-9]+\.[0-9]+\.[0-9]+$
#H   if the format is wrong it will be ignored.
#H   
#H requirements:
#H
#H   - <module>'s pom file contains the profiles 'release' and 'snapshot'
#H   - the files
#H       * mvnsettings.xml
#H       * codesigning.asc.enc
#H     have to exist in the same folder as this script
#H   - the environment variables 
#H       * encrypted_21a5d40e43a3_key 
#H       * encrypted_21a5d40e43a3_iv
#H     have to exist (in order to decode codesigning.asc.enc)
# Arguments:
#   $1: exit code
function helpAndExit {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

# decripting gpg keys and importing them (needed to sign artifacts)
# Global:
#   $encrypted_21a5d40e43a3_key: decription key
#   $encrypted_21a5d40e43a3_iv: initialisation vector
# Arguments:
#   $1: basedir
function decodeAndImportKeys {
  if [[ ! -f "$1/codesigning.asc" ]]; then
    openssl aes-256-cbc -K "$encrypted_21a5d40e43a3_key" -iv "$encrypted_21a5d40e43a3_iv" -in "$1/codesigning.asc.enc" -out "$1/codesigning.asc" -d
    gpg --import "$1/codesigning.asc"
  fi
}

# deploying a given project
# Arguments:
#   $1: project folder (dir)
#   $2: profile name
#   $3: settings file (dir)
function release {
  mvn deploy -f "$1" -P "$2" --settings "$3" -DskipTests=true -B -U
}

# changing version in pom and all its children
# Arguments:
#   $1: directory of pom
#   $2: new version
function change_version {
  mvn versions:set -f "$1" -DnewVersion="$2"   -DartifactId=*  -DgroupId=* versions:commit
}

function main {
  [[ $# -eq 0 || "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  [[ "$2" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]] && change_version "$1" "${2##v}"
  decodeAndImportKeys `dirname "$0"`
  release "$1" `[[ "$2" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]] && echo "release" || echo "snapshot"` "`dirname "$0"`/mvnsettings.xml"
}

main "$@"
