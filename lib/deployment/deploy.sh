#!/bin/bash
set -e #fail fast

reqRepo="Taskana/taskana"
[[ -z "$MANIFEST_PREFIX" ]] && MANIFEST_PREFIX="/rest"
#H Usage:
#H deploy.sh -h | pfcalc.sh --help
#H
#H prints this help and exits  
#H 
#H deploy.sh [PARAM...]
#H
#H   an easy deployment tool to deploy maven projects.
#H
#H   On a tagged commit
#H     version will be set to the one in the tag
#H     maven deploy with the profile 'release' will be excecuted
#H   On a non-tagged commit on the master branch
#H     maven deploy with the profile 'snapshot' will be excecuted
#H
#H
#H PARAM can be one of the following:
#H   -avc | --append-version-change
#H     List of modules (path) whose version will be updated after deployment.
#H   -d   | --dry-run
#H     echos out all commands instead of excecuting them.
#H   -m   | --modules
#H     List of modules (path) which will be deployed.
#H   -mf  | --manifest
#H     if a manifest file exists the version of an artifact will be replaced.
#H     You can Overwrite it by setting the env variable MANIFEST_PREFIX to the required prefix.
#H   -p   | --parent
#H     If a parent pom exists the version change will be done in the parent instead of every module.
#H 
#H
#H IMPORTANT: 
#H   - All Lists have to be passed as one parameter. 
#H   - When a parameter is duplicated its last occurance will count
#H
#H Environment variables:
#H   - encrypted_57343c8b243e_key
#H       private key needed for decoding 'codesigning.asc.enc' file in script directory
#H   - encrypted_57343c8b243e_iv
#H       initialisation vektor to decode 'codesigning.asc.enc' file in scirpt directory
#H   - GH_TOKEN
#H       token to write back to the git repo after release deployment
#H   - MANIFEST_PREFIX
#H       if a manifest file is set the pattern matching can be modified.
#H       The pattern will then replace '$MANIFEST_PREFIX.*\.jar' with '$MANIFEST_PREFIX-$VERSION-SNAPSHOT.jar'.
#H       Default value is '/rest'
#H   - TRAVIS_REPO_SLUG
#H       git repo slug
#H   - TRAVIS_PULL_REQUEST
#H       'false' if this is not a PR build. Otherwise this is a PR build.
#H   - TRAVIS_TAG 
#H       if this is a tagged build then TRAVIS_TAG contains the version number.
#H       pattern: v[DIGIT].[DIGIT].[DIGIT]
#H   - TRAVIS_BRANCH
#H       branch of this build (only used if TRAVIS_TAG is not set)
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

# takes a version (without leading v) and increments its
# last number by one. 
# Arguments:
#   $1: version (without leading v) which will be patched
# Return:
#   version with last number incremented
function increment_version() {
  if [[ ! "$1" =~ [0-9]+\.[0-9]+\.[0-9]+ ]]; then
    echo "'$1' does not match tag pattern." >&2
    exit 1;
  fi
  echo "${1%\.*}.`expr ${1##*\.*\.} + 1`"
}

# changing version in pom and all its children
# Arguments:
#   $1: directory of pom
#   $2: new version
function change_version {
  $debug mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -f "$1" -DnewVersion="$2"   -DartifactId=*  -DgroupId=*
}

# adds all pom(s) to a git commit and pushes back to the github
# Global:
#   $branch: branch where commit will land
#   $GH_TOKEN: github token (to authenticate)
# Arguments:
#   Additional files which will be committed aswell
function push_new_poms() {
  #commit all poms
  $debug git checkout -b "$branch"
  #to compensate new updates
  $debug git pull
  $debug git add "./*pom.xml"
  for file in "$@"; do
    [[ -n "$file" ]] && $debug git add "$file"
  done
  $debug git commit -m "Updated poms to version `increment_version ${TRAVIS_TAG##v}`-SNAPSHOT"

  #push poms (authentication via GH_TOKEN)
  $debug git remote add deployment "https://$GH_TOKEN@github.com/$reqRepo.git"
  $debug git push --quiet --set-upstream deployment "$branch"
}

# prints all relevant environment methods
# Global:
# -> see help
function print_environment() {
  echo "####################################"
  echo "dry-run detected."
  echo "environment:"
  echo "  GH_TOKEN: '$GH_TOKEN'"
  echo "  MANIFEST_PREFIX: '$MANIFEST_PREFIX'"
  echo "  TRAVIS_BRANCH: '$TRAVIS_BRANCH'"
  echo "  TRAVIS_TAG: '$TRAVIS_TAG'"
  echo "  TRAVIS_PULL_REQUEST: '$TRAVIS_PULL_REQUEST'"
  echo "  TRAVIS_REPO_SLUG: '$TRAVIS_REPO_SLUG'"
  echo "####################################"
}

function main {

  if [[ $# -eq 0 ]]; then
    helpAndExit 0
  fi 

  while [[ $# -gt 0 ]]; do
    case $1 in
      -avc|--additional-version-change)
        if [[ -z "$2" || "$2" == -* ]]; then
          echo "missing parameter for argument '-avc|--additional-version-change'" >&2
          exit 1
        fi
        local ADDITIONAL_VC=($2)
        shift # past argument
        shift # past value
        ;;
      -d|--dry-run)
        DRY_RUN=YES
        shift # past argument
        ;;
      -h|--help)
        helpAndExit 0
        ;;
      -m|--modules)
        if [[ -z "$2" || "$2" == -* ]]; then
          echo "missing parameter for argument '-m|--modules'" >&2
          exit 1
        fi
        local MODULES=($2)
        shift # past argument
        shift # past value
        ;;
      -mf|--manifest)
        if [[ -z "$2" || "$2" == -* ]]; then
          echo "missing parameter for argument '-mf|--manifest'" >&2
          exit 1
        fi
        local MANIFEST="$2"
        shift # past argument
        shift # past value
        ;;
      -p|--parent)
        if [[ -z "$2" || "$2" == -* ]]; then
          echo "missing parameter for argument '-p|--parent'" >&2
          exit 1
        fi
        local PARENT_DIR="$2"
        shift # past argument
        shift # past value
        ;;
      -swarm)
        local SWARM="$2"
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

  local debug=
  if [[ -n "$DRY_RUN" ]]; then
    debug=echo
    print_environment
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
        #dummy value for dry run
        branch="BRANCH"
        echo "!!! - Skipping automatic detection of tag branch. Instead using '$branch'"
    fi

    if [[ -n "$PARENT_DIR" ]]; then
      change_version "$PARENT_DIR" "${TRAVIS_TAG##v}"
    else
      for dir in ${MODULES[@]}; do
        change_version "$dir" "${TRAVIS_TAG##v}"
      done
    fi
  else
    if [[ "$TRAVIS_BRANCH" != 'master' ]]; then
      echo "Skipping release to sonatype because this branch is not permitted"
      exit 0
    fi
    local profile="snapshot"
  fi 
  

  decodeAndImportKeys `dirname "$0"`
  for dir in ${MODULES[@]}; do
    deploy "$dir" "$profile" "`dirname "$0"`/mvnsettings.xml"
  done

  if [[ -n "$branch" ]]; then
    if [[ -z "$GH_TOKEN" ]]; then
      echo 'GH_TOKEN not set' >&2
      exit 1
    fi

    local newVersion=`increment_version ${TRAVIS_TAG##v}`

    if [[ -n "$PARENT_DIR" ]]; then
      change_version "$PARENT_DIR" "$newVersion-SNAPSHOT"
    else
      for dir in ${MODULES[@]}; do
        change_version "$dir" "$newVersion-SNAPSHOT"
      done
    fi

    for dir in ${ADDITIONAL_VC[@]}; do
      change_version "$dir" "$newVersion-SNAPSHOT"
    done

    if [[ -n "$SWARM" ]]; then
        $debug sed -i "s/pro.taskana:taskana-core.*-SNAPSHOT/pro.taskana:taskana-core:$newVersion-SNAPSHOT/" "$SWARM"
    fi

    if [[ -n "$MANIFEST" ]]; then
        $debug sed -i "s|$MANIFEST_PREFIX.*\.jar|$MANIFEST_PREFIX-$newVersion-SNAPSHOT.jar|" "$MANIFEST"
    fi
    
    push_new_poms "$MANIFEST" "$SWARM"
  fi
}

main "$@"
