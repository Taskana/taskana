#!/bin/bash
set -e #fail fast

reqRepo="Taskana/taskana"

#H Usage:
#H deploy.sh -h | pfcalc.sh --help
#H
#H prints this help and exits  
#H 
#H deploy.sh [--dry-run] <parent dir> <project dir> [project dir ...]
#H
#H   an easy deployment tool to deploy maven projects.
#H
#H   On a tagged commit
#H     version will be set to the one in the tag
#H     maven deploy with the profile 'release' will be excecuted
#H   On a non-tagged commit on the master branch
#H     maven deploy with the profile 'snapshot' will be excecuted
#H 
#H This script works with the following env variables:
#H   - TRAVIS_REPO_SLUG
#H       git repo slug
#H   - TRAVIS_PULL_REQUEST
#H       filled with anything, if this is a PR build
#H   - TRAVIS_TAG 
#H       filled with anything, if this is a tagged build
#H   - TRAVIS_BRANCH
#H       branch of this build (only used if TRAVIS_TAG is not set)
#H   - GH_TOKEN
#H       token to write back to the git repo after release deployment
# Arguments:
#   $1: exitcode
function helpAndExit {
  cat "$0" | grep "^#H" | cut -c4-
  exit "$1"
}

# decripting gpg keys and importing them (needed to sign artifacts)
# Global:
#   $encrypted_57343c8b243e_key: decription key 
#   $encrypted_57343c8b243e_iv: initialisation vector
# Arguments:
#   $1: basedir
function decodeAndImportKeys {
  $debug openssl aes-256-cbc -K "$encrypted_57343c8b243e_key" -iv "$encrypted_57343c8b243e_iv" -in "$1/codesigning.asc.enc" -out "$1/codesigning.asc" -d
  $debug gpg --import "$1/codesigning.asc"
}


# deploying a given project
# Arguments:
#   $1: project folder (dir)
#   $2: profile name
#   $3: settings file (dir)
function deploy {
  $debug mvn deploy -f "$1" -P "$2" --settings "$3" -DskipTests=true -B -U
}

# changing version in pom and all its children
# Arguments:
# $1: directory of pom
# $2: new version
function change_version {
  $debug mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -f "$1" -DnewVersion="$2"   -DartifactId=*  -DgroupId=*
}

function push_new_poms() {
  #setup username
  $debug git config --global user.email "travis@travis-ci.org"
  $debug git config --global user.name "Travis CI"

  #commit all poms
  $debug git checkout -b "$branch"
  #to compensate new updates
  $debug git pull
  $debug git add "./*pom.xml"
  $debug git commit -m "Updated poms to version ${TRAVIS_TAG##v}-SNAPSHOT"

  #push poms (authentication via GH_TOKEN)
  $debug git remote add deployment "https://$GH_TOKEN@github.com/$reqRepo.git"
  $debug git push --quiet --set-upstream deployment "$branch"
}

function print_variables() {
  echo "####################################"
  echo "dry-run detected."
  echo "environment:"
  echo "  tag: '$TRAVIS_TAG'"
  echo "  branch: '$TRAVIS_BRANCH'"
  echo "  repo: '$TRAVIS_REPO_SLUG'"
  echo "  github token: '$GH_TOKEN'"
  echo "  PR build: '$TRAVIS_PULL_REQUEST'"
  echo "####################################"
}

function main {
  if [[ "$1" = '--help' || "$1" = '-h' || $# -eq 0 ]]; then
    helpAndExit 0
  fi 

  local debug=
  if [[ "$1" = '--dry-run' ]]; then
    debug=echo
    print_variables
    shift
  fi

  if [[ "$#" -lt 2 ]]; then
    helpAndExit 1
  fi


  if [[ "$TRAVIS_REPO_SLUG" != "$reqRepo" ]]; then
    echo "Skipping release to sonatype because this repo's name does not match with: $reqRepo"
    exit 0
  fi

  if [[ "$TRAVIS_PULL_REQUEST" != 'false' ]]; then
    echo "Skipping release to sonatype because this is a PR build"
    exit 0
  fi

  if [[ -z "$debug" && (-z "$encrypted_57343c8b243e_key" || -z "$encrypted_57343c8b243e_iv") ]]; then
    echo "you are not travis or travis does not have the correct encryption key and iv" >&2
    exit 1
  fi

  if [[ "$TRAVIS_TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    local parent_dir="$1"
    local profile="release"

    if [[ -z "$debug" ]]; then
      #check if tagged commit is a head commit of any branch
      local commit=`git ls-remote -q -t origin | grep "$TRAVIS_TAG" | cut -c1-40`
      local branch=`git ls-remote -q -h origin | grep "$commit" | sed "s/$commit.*refs\/heads\///"`

      if [[ -z "$commit" || -z "$branch" ]]; then
        echo "the commit '$commit' of tag '$TRAVIS_TAG' is not a head commit. Can not release" >&2
        exit 1
      fi

      if [[ `echo "$branch" | wc -l` != '1' ]]; then
        echo "can not match commit '$commit' to a unique branch." >&2
        echo "Please make sure, that the tag '$TRAVIS_TAG' is the head of a unique branch" >&2
        echo "Branches detected: $branch"
        exit 1
      fi
    else
        branch="BRANCH"
        echo "!!! - Skipping automatic detection of tag branch. Instead using '$branch'"
    fi

    change_version "$parent_dir" "${TRAVIS_TAG##v}"
  else
    if [[ "$TRAVIS_BRANCH" != 'master' ]]; then
      echo "Skipping release to sonatype because this branch is not permitted"
      exit 0
    fi
    local profile="snapshot"
  fi 
  shift

  decodeAndImportKeys `dirname "$0"`
  for dir in "$@"; do
    deploy "$PWD/$dir" "$profile" "`dirname "$0"`/mvnsettings.xml"
  done

  if [[ -n "$branch" && -n "$parent_dir" ]]; then
    change_version "$parent_dir" "${TRAVIS_TAG##v}-SNAPSHOT"
    if [[ -z "$GH_TOKEN" ]]; then
      echo 'GH_TOKEN not set' >&2
      exit 1
    fi
    push_new_poms
  fi
}

main "$@"
