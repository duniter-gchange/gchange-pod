#!/bin/bash

PROJECT_NAME=gchange-pod
SCRIPT_LOCATION=$(dirname $0)
PROJECT_DIR=$(cd ${SCRIPT_LOCATION} && pwd)
REPO="duniter-gchange/${PROJECT_NAME}"
REPO_URL=https://api.github.com/repos/${REPO}

### Control that the script is run on `dev` branch
branch=`git rev-parse --abbrev-ref HEAD`
if [[ "$branch" = "master" ]];
then
  echo ">> This script must be run under a branch (tag)"
  exit 1
fi

### Get version to release
current=`grep -m1 -P "\<version>[0-9A−Z.]+(-\w*)?</version>" pom.xml | grep -oP "\d+.\d+.\d+(-\w*)?"`
echo "Current version: $current"
remote_tag=${PROJECT_NAME}-$current


###  get auth token
GITHUB_TOKEN=`cat ~/.config/${PROJECT_NAME}/.github`
if [[ "_$GITHUB_TOKEN" != "_" ]]; then
    GITHUT_AUTH="Authorization: token $GITHUB_TOKEN"
else
    echo "Unable to find github authentication token file: "
    echo " - You can create such a token at https://github.com/settings/tokens > 'Generate a new token'."
    echo " - Then copy the token and paste it in the file '~/.config/${PROJECT_NAME}/.github' using a valid token."
    exit 1
fi

case "$1" in
  del)
    result=`curl -i "$REPO_URL/releases/tags/$remote_tag"`
    release_url=`echo "$result" | grep -P "\"url\": \"[^\"]+"  | grep -oP "$REPO_URL/releases/\d+"`
    if [[ $release_url != "" ]]; then
        echo "Deleting existing release..."
        curl -H 'Authorization: token $GITHUB_TOKEN'  -XDELETE $release_url
    fi
  ;;

  pre|rel)

    if [[ $1 = "pre" ]]; then
      prerelease="true"
    else
      prerelease="false"
    fi

    description=`echo $2`
    if [[ "_$description" = "_" ]]; then
        description="Release v$current"
    fi

    result=`curl -s -H ''"$GITHUT_AUTH"'' "$REPO_URL/releases/tags/$remote_tag"`
    release_url=`echo "$result" | grep -P "\"url\": \"[^\"]+" | grep -oP "https://[A-Za-z0-9/.-]+/releases/\d+"`
    if [[ "_$release_url" != "_" ]]; then
        echo "Deleting existing release... $release_url"
        result=`curl -H ''"$GITHUT_AUTH"'' -s -XDELETE $release_url`
        if [[ "_$result" != "_" ]]; then
            error_message=`echo "$result" | grep -P "\"message\": \"[^\"]+" | grep -oP ": \"[^\"]+\""`
            echo "Delete existing release failed with error$error_message"
            exit 1
        fi
    else
        echo "Release not exists yet on github."
    fi

    echo "Creating new release..."
    echo " - tag: $remote_tag"
    echo " - description: $description"
    result=$(curl -H ''"$GITHUT_AUTH"'' -s $REPO_URL/releases -d '{"tag_name": "'"$remote_tag"'","target_commitish": "master","name": "'"$current"'","body": "'"$description"'","draft": false,"prerelease": '"$prerelease"'}')
    #echo "DEBUG - $result"
    upload_url=$(echo "$result" | grep -P "\"upload_url\": \"[^\"]+"  | grep -oP "https://[A-Za-z0-9/.-]+")
    if [[ "_$upload_url" = "_" ]]; then
      echo "Failed to create new release for repo $REPO."
      echo "Server response:"
      echo "$result"
      exit 1
    fi

    ###  Sending files
    echo "Uploading files... to $upload_url"

    ZIP_BASENAME="${PROJECT_NAME}-$current-standalone.zip"
    ZIP_FILE="${PROJECT_DIR}/gchange-pod-assembly/target/${ZIP_BASENAME}"
    if [[ -f "${ZIP_FILE}" ]]; then
      result=$(curl -s -H ''"$GITHUT_AUTH"'' -H 'Content-Type: application/zip' -T "${ZIP_FILE}" "$upload_url?name=${ZIP_BASENAME}")
      browser_download_url=$(echo "$result" | grep -P "\"browser_download_url\":[ ]?\"[^\"]+" | grep -oP "\"browser_download_url\":[ ]?\"[^\"]+"  | grep -oP "https://[A-Za-z0-9/.-]+")
      ZIP_SHA256=$(cd ${PROJECT_DIR}/gchange-pod-assembly/target && sha256sum "${ZIP_BASENAME}")
      echo " - ${browser_download_url}  | Checksum: ${ZIP_SHA256}"
    else
      echo "ERROR: Web release (ZIP) not found!"
      exit 1
    fi

    # Send Checksum file
    SHA_BASENAME=${PROJECT_NAME}-$current-standalone.sha256
    SHA_FILE=${PROJECT_DIR}/gchange-pod-assembly/target/${SHA_BASENAME}
    echo "${ZIP_SHA256}" > ${SHA_FILE}
    result=$(curl -s -H ''"$GITHUT_AUTH"'' -H 'Content-Type: text/plain' -T "${SHA_FILE}" "${upload_url}?name=${SHA_BASENAME}")

    echo "-----------------------------------------"
    echo "Successfully uploading files to github !"

    ;;
  *)
    echo "Usage:  $0 {del|pre|rel} <release_description>"
    echo "With:"
    echo " - del: delete existing release"
    echo " - pre: use for pre-release"
    echo " - rel: for full release"
    exit 1
    ;;
esac
