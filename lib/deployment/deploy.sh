#!/bin/bash
set -e #fail fast

reqRepo="Taskana/taskana"

#H Usage:
#H   deploy.sh [OPTION] <parent dir> <project dir> [project dir ...]
#H Where OPTION is one of
#H   --help: prints this help messge
#H   --dry-run: echos all commands instead of excecution
function helpAndExit {
  cat "$0" | grep "^#H" | cut -c4-
  exit 0
}

# decripting gpg keys and importing them (needed to sign artifacts)
# Global:
#   $encrypted_fbbd56f3fa0c_key: decription key 
#   $encrypted_fbbd56f3fa0c_iv: initialisation vector
# Arguments:
#   $1: basedir
function decodeAndImportKeys {
  $debug openssl aes-256-cbc -K "$encrypted_fbbd56f3fa0c_key" -iv "$encrypted_fbbd56f3fa0c_iv" -in "$1/codesigning.asc.enc" -out "$1/codesigning.asc" -d
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
  $debug git add "./*pom.xml"
  $debug git commit -m "Updated poms to version ${TRAVIS_TAG##v}-SNAPSHOT"

  #push poms (authentication via GH_TOKEN)
  $debug git remote add origin-pages "https://$GH_TOKEN@github.com/$reqRepo.git" >/dev/null 2>&1
  $debug git push --quiet --set-upstream origin-pages "$branch"
}

function main {
  if [[ "$1" = '--help' || $# -eq 0 ]]; then
    helpAndExit
  fi 

  local debug=
  if [[ "$1" = '--dry-run' ]]; then
    debug=echo
    shift
  fi

  if [[ -z "$debug" && ("$TRAVIS" != 'true' || -z "$encrypted_fbbd56f3fa0c_key" || -z "$encrypted_fbbd56f3fa0c_iv") ]]; then
    echo "you are not travis or travis does not have the correct encryption key and iv" >&2
    exit 1
  fi

  if [[ "$TRAVIS" == 'true' && "$TRAVIS_REPO_SLUG" != "$reqRepo" ]]; then
    echo "Skipping release to sonatype because this repo's name does not match with: $reqRepo"
    exit 0
  fi

  if [[ "$TRAVIS" == 'true' && -n $TRAVIS_PULL_REQUEST ]]; then
    echo "Skipping release to sonatype because this is a PR build"
    exit 0
  fi

  if [[ "$TRAVIS_TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    #check if tagged commit is a head commit of any branch
    local commit=`git ls-remote origin | grep "$TRAVIS_TAG" | cut -c1-40`
    local branch=`git ls-remote origin | grep -v refs/tags | grep "$commit" | sed "s/$commit.*refs\/heads\///"`
    if [[ -z "$branch" ]]; then
      echo "the commit of tag '$TRAVIS_TAG' is not a head commit. Can not release" >&2
      exit 1;
    fi
    local parent_dir="$1"
    local profile="release"
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
