#!/bin/bash

# Get to the root project
if [[ "_" == "_${PROJECT_DIR}" ]]; then
  SCRIPT_DIR=$(dirname $0)
  PROJECT_DIR=$(cd "${SCRIPT_DIR}/../.." && pwd)
  export PROJECT_DIR
fi;

if [[ -f "${PROJECT_DIR}/.local/env.sh" ]]; then
  source ${PROJECT_DIR}/.local/env.sh
fi;

cd ${PROJECT_DIR}

task=$1
description=$2

# Check arguments
if [[ ! $task =~ ^(pre|rel)$ ]]; then
  echo "Wrong version format"
  echo "Usage:"
  echo " > $0 pre|rel <release_description>"
  echo "with:"
  echo " - pre: use for pre-release"
  echo " - rel: for full release"
  echo " - release_description: a comment on release"
  exit 1
fi

RELEASE_OPTS="-DskipTests -DperformFullRelease"

# Clean previous install
mvn clean --quiet
[[ $? -ne 0 ]] && exit 1

# Rollback previous release, if need
if [[ -f "pom.xml.releaseBackup" ]]; then
    echo "**********************************"
    echo "* Rollback previous release..."
    echo "**********************************"
    result=`mvn release:rollback`
    failure=`echo "$result" | grep -m1 -P "\[INFO\] BUILD FAILURE"  | grep -oP "BUILD \w+"`
    # rollback failed
    if [[ ! "_$failure" = "_" ]]; then
        echo "$result" | grep -P "\[ERROR\] "
        exit 1
    fi
    echo "Rollback previous release [OK]"
fi

echo "**********************************"
echo "* Preparing release..."
echo "**********************************"
result=$(mvn release:clean)
failure=$(echo "$result" | grep -m1 -P "\[INFO\] BUILD FAILURE"  | grep -oP "BUILD \w+")
# prepare failed
if [[ ! "_$failure" = "_" ]]; then
    echo "$result" | grep -P "\[ERROR\] "
    exit 1
fi

mvn release:prepare -Darguments="${RELEASE_OPTS}"
[[ $? -ne 0 ]] && exit 1

echo "**********************************"
echo "* Performing release..."
echo "**********************************"
mvn release:perform -Darguments="${RELEASE_OPTS}"
[[ $? -ne 0 ]] && exit 1

echo "**********************************"
echo "* Uploading artifacts to Github..."
echo "**********************************"
cd target/checkout
. ${PROJECT_DIR}/src/scripts/release-to-github.sh $task $description
[[ $? -ne 0 ]] && exit 1

echo "RELEASE finished !"

