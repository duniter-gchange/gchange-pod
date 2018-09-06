#!/bin/bash

VERSION=$1
OLD_VERSION=$2

if [ "${VERSION}" == "" ]; then
        echo "ERROR: Missing version argument !"
        echo " "
        echo "usage: sudo ./update-gchange-pod.sh <version> [<old_version>]"
        exit
fi
if [ "${OLD_VERSION}" == "" ]; then
        OLD_VERSION=`ps -efl | grep gchange-pod | grep lib/elasticsearch > /tmp/ps_out && cat /tmp/ps_out | sed -r 's/.*gchange-pod-([0-9.]+) .*/\1/g'`
        if [ "${OLD_VERSION}" == "" ]; then
                echo "Error: unable to known previous version"
                exit
        fi
fi

READLINK=`which readlink`
if [ -z "$READLINK"  ]; then
  message "Required tool 'readlink' is missing. Please install before launch \"$0\" file."
  exit 1
fi

# ------------------------------------------------------------------
# Ensure BASEDIR points to the directory where the soft is installed.
# ------------------------------------------------------------------
SCRIPT_LOCATION=$0
if [ -x "$READLINK" ]; then
  while [ -L "$SCRIPT_LOCATION" ]; do
    SCRIPT_LOCATION=`"$READLINK" -e "$SCRIPT_LOCATION"`
  done
fi 

export BASEDIR=`dirname "$SCRIPT_LOCATION"`                                                                                                                                                                                                        
cd $BASEDIR 

echo "--- Downloading gchange-pod-standalone v$VERSION... ----------------------"

if [ -f "downloads/gchange-pod-${VERSION}-standalone.zip" ]; then
        echo "...removing file, as it already exists in ./downloads/gchange-pod-${VERSION}-standalone.zip"
        rm ./downloads/gchange-pod-${VERSION}-standalone.zip
fi

if [ ! -e "downloads" ]; then
        mkdir downloads
fi

cd downloads
wget -kL https://github.com/duniter-gchange/gchange-pod/releases/download/gchange-pod-${VERSION}/gchange-pod-${VERSION}-standalone.zip
cd ..

if [ -f "downloads/gchange-pod-${VERSION}-standalone.zip" ]; then
        echo ""
else
        echo "Error: unable to download this version!"
        exit -1
fi

echo "--- Installating gchange-pod v$VERSION... ---------------------"
if [ -d "/opt/gchange-pod-${VERSION}" ]; then
        echo "Error: Already installed in /opt/gchange-pod-${VERSION} !"
        exit -1
fi

unzip -o ./downloads/gchange-pod-${VERSION}-standalone.zip
mv gchange-pod-${VERSION} gchange-pod-${VERSION}
sudo mv gchange-pod-${VERSION} /opt/
sudo rm /opt/gchange-pod
sudo ln -s /opt/gchange-pod-${VERSION} /opt/gchange-pod

mkdir /opt/gchange-pod-${VERSION}/data
mv /opt/gchange-pod-${VERSION}/config/elasticsearch.yml /opt/gchange-pod-${VERSION}/config/elasticsearch.yml.ori

./stop-gchange-pod.sh

if [ "$OLD_VERSION" != "$VERSION" ];
then
        echo "--- Restoring files (data+config) from previous version $OLD_VERSION... ---------------------"
        tar -cvf /opt/gchange-pod-${OLD_VERSION}/data/save.tar.gz /opt/gchange-pod-${OLD_VERSION}/data/gchange-*
        mv /opt/gchange-pod-${OLD_VERSION}/data/gchange-* /opt/gchange-pod-${VERSION}/data
        cp /opt/gchange-pod-${OLD_VERSION}/config/elasticsearch.yml /opt/gchange-pod-${VERSION}/config
fi

#./start-gchange-pod.sh

echo "--- Successfully installed gchange-pod v$VERSION ! -------------"
echo ""

