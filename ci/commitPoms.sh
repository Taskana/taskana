#!/bin/bash
set -e # fail fast

#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE% [additional files...]
#H
#H   commits and pushes all *.pom files (+ additional files)
#H
#H Requirements:
#H   current commit is a HEAD commit
#H   GH_TOKEN - github access token
#H   GH_USER - username for the github access token
#H   GH_USERNAME - github username / displayname (for git config)
#H   GH_EMAIL - github email address (for git config)
#H   TRAVIS_TAG (format v[0-9]+\.[0-9]+\.[0-9]+)
#H   TRAVIS_REPO_SLUG - repo name (in form: owner_name/repo_name)
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
  [[ -z "$GH_USER" || -z "$GH_TOKEN" || -z "$GH_EMAIL" || -z "$GH_USERNAME" || -z "$TRAVIS_REPO_SLUG" ]] && helpAndExit 1
  if [[ "$TRAVIS_TAG" =~ v[0-9]+\.[0-9]+\.[0-9]+ ]]; then
    #check if tagged commit is a head commit of any branch
    commit=$(git ls-remote -q -t origin | grep "$TRAVIS_TAG" | cut -c1-40)
    branch=$(git ls-remote -q -h origin | grep "$commit" | sed "s/$commit.*refs\/heads\///")

    if [[ -z "$commit" || -z "$branch" ]]; then
      echo "the commit '$commit' of tag '$TRAVIS_TAG' is not a head commit. Can not release" >&2
      exit 1
    fi

    if [[ $(echo "$branch" | wc -l) != '1' ]]; then
      echo "can not match commit '$commit' to a unique branch." >&2
      echo "Please make sure, that the tag '$TRAVIS_TAG' is the head of a unique branch" >&2
      echo "Branches detected: $branch"
      exit 1
    fi
    set -x
    git config --global user.email $GH_EMAIL
    git config --global user.name $GH_USERNAME

    #commit all poms
    git checkout "$branch"
    git add "./*pom.xml"
    for file in "$@"; do
      [[ -n "$file" ]] && git add "$file"
    done
    git commit -m "Updated poms to version $(increment_version ${TRAVIS_TAG##v})-SNAPSHOT"

    #push poms (authentication via GH_TOKEN)
    git remote add deployment "https://$GH_USER:$GH_TOKEN@github.com/$TRAVIS_REPO_SLUG.git"
    git push --quiet --set-upstream deployment "$branch"
  else
    echo "Nothing to push - this is not a release!"
  fi

}

main "$@"
