#!/bin/bash

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

mvn release:prepare -Darguments="-DskipTests -DperformFullRelease"
if [[ $? -ne 0 ]]; then
    exit 1
fi

echo "**********************************"
echo "* Performing release..."
echo "**********************************"
mvn release:perform --quiet -Darguments="-DskipTests -DperformFullRelease"
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

