#!/bin/sh

# You can override the following settings with the correct location of Java
if [ -z "$RTIJDKHOME" ]; then
    JAVAC=`which javac`
else
    JAVAC="$RTIJDKHOME/bin/javac"
fi

# Make sure JAVAC and NDDSHOME are set correctly
test -z "$JAVAC" && echo "javac not found" && exit 1
test -z "$NDDSHOME" && echo "NDDSHOME environment variable not set" && exit 1

# Ensure this script is invoked from the root directory of the project
test ! -d src && echo "You must run this script from the example root directory" && exit 1

if [ ! -f src/es/ugr/disha/idl/FileSegment.java ]; then
    # Re-generate the type support code from the IDL file
    echo "Remaking the type-support code..."
    cd src
#    mkdir -p com/rti/hello/idl
    $NDDSHOME/scripts/rtiddsgen -package es.ugr.ddsbox.idl -language Java -replace res/disha.idl
    cd ..
fi


