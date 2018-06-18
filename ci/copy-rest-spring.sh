#!/bin/bash
set -e # fail fast

if [[ ! "$1" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then 
  echo "missing tag" >&2 
  exit 1
fi

cp ~/.m2/repository/pro/taskana/taskana-rest-spring-example/${1##v}/taskana-rest-spring-example-${1##v}.jar taskana-rest-spring-example.jar
