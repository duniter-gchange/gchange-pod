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

RELEASE_OPTS="-DskipTests -DperformFullRelease"

# Clean previous install
mvn clean --quiet
if [[ $? -ne 0 ]]; then
    exit 1
fi

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
result=`mvn release:clean`
failure=`echo "$result" | grep -m1 -P "\[INFO\] BUILD FAILURE"  | grep -oP "BUILD \w+"`
# prepare failed
if [[ ! "_$failure" = "_" ]]; then
    echo "$result" | grep -P "\[ERROR\] "
    exit 1
fi

mvn release:prepare -Darguments="${RELEASE_OPTS}"
if [[ $? -ne 0 ]]; then
    exit 1
fi

echo "**********************************"
echo "* Performing release..."
echo "**********************************"
mvn release:perform --quiet -Darguments="${RELEASE_OPTS}"
if [[ $? -ne 0 ]]; then
    exit 1
fi

echo "**********************************"
echo "* Uploading artifacts to Github..."
echo "**********************************"
cd target/checkout
./github.sh pre
if [[ $? -ne 0 ]]; then
    exit 1
fi

echo "RELEASE finished !"

