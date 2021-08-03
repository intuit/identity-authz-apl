#!/usr/bin/env bash
#!/bin/bash -e
set -x
whoami
# Define params based on whether this is a snapshot or Release build
# Make it case insensitive (master or Master)
echo "Running a Release build from shell script"

export VERSION_SRC_DIR=`dirname $0`
. $VERSION_SRC_DIR/build.properties
if [ -z $RELEASE_VERSION  ]; then
    echo "Make sure build.properties is present in the root directory\
    of the git repo and contains value for variable RELEASE_VERSION"
    exit 1
else
    num_digits=$(echo $RELEASE_VERSION | tr '.' '\n' | wc -l)
    if [ $num_digits != 3 ]; then
        echo "Give only three digits for variable RELEASE_VERSION, example 1.2.3"
        exit 1
    fi
fi

# To get ordered builds in Nexus yyyy-mm-dd format is used
DATE=$(date +%Y-%m-%d)
#FINAL_VERSION=$RELEASE_VERSION.$BUILD_NUMBER-$DATE
export FINAL_VERSION=$RELEASE_VERSION.$BUILD_NUMBER
echo "Building version $FINAL_VERSION"
find . -type f -name "pom.xml" -exec sed -i -e "s/1\.0-SNAPSHOT/${FINAL_VERSION}/g" {} +

#Adding version to the version.properties
echo "aplJavaSDKVersion=$FINAL_VERSION" > ./src/main/resources/version.properties
