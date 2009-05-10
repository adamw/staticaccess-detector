#!/bin/bash
# Runs the StaticAccess Detector tests

# First creating the jar
ant

# Determining the full path to the FindBugs home from the properties file
FINDBUGS_HOME=`cat build.properties | grep findbugs.home | cut -b 15-`
FINDBUGS_HOME=`cd $FINDBUGS_HOME; pwd`
FINDBUGS_JAR=$FINDBUGS_HOME/lib/findbugs.jar
echo "StaticAccess Detector tests, FindBugs jar: '$FINDBUGS_JAR'"

# Building the test classes
cd build
mkdir -p test
cd ../test/src
SOURCE_TEST_FILES=`find . -name *Test.java`
echo "Compiling test sources ..."
javac -cp ../../build/annotations:$FINDBUGS_JAR -d ../../build/test $SOURCE_TEST_FILES
cd ../..

# Now running findbugs and grabbing the output
echo "Running findbugs ...."
COMPILED_TEST_FILES=`find build/test -name *.class`
FB_AUXCLASPATH=dist/staticaccess-detector-annotations.jar
FB_PLUGINLIST=dist/staticaccess-detector.jar
FB_OUTPUT=`java -jar $FINDBUGS_JAR -textui -auxclasspath $FB_AUXCLASPATH -pluginList $FB_PLUGINLIST $COMPILED_TEST_FILES 2>/dev/null`

# And finally passing the output to the checking program
echo "Checking the output ..."
for SOURCE_TEST_FILE in $SOURCE_TEST_FILES
do
    TEST_NAME=`echo $SOURCE_TEST_FILE | sed 's/[^A-Z]*\([a-zA-Z]*\)Test\.java/\1/'`
    OUT_FILE_NAME=`echo $SOURCE_TEST_FILE | sed 's/\(.[^.]*\)\.java/\1.out/'`

    # Getting only bugs reported by the static access detector
    SAD_FB_OUTPUT=`echo "$FB_OUTPUT" | grep 'SANA\|NSIMI' | grep $TEST_NAME`
    # Extracting the bug name and line number
    SAD_FB_OUTPUT=`echo "$SAD_FB_OUTPUT" | sed 's/. . \([A-Z]*\):[^:]*:\[line \([0-9]*\)\]/\1 \2/'`    
    # Getting the expected output
    SAD_FB_EXPECTED_OUTPUT=`cat test/src/$OUT_FILE_NAME`

    # Comparing the results
    if [ "$SAD_FB_OUTPUT" == "$SAD_FB_EXPECTED_OUTPUT" ]
    then
        echo "$TEST_NAME: OK"
    else
        SAD_FB_EXPECTED_OUTPUT=`echo "$SAD_FB_EXPECTED_OUTPUT" | tr "\n" ";"`
        SAD_FB_OUTPUT=`echo "$SAD_FB_OUTPUT" | tr "\n" ";"`
        echo "$TEST_NAME: ERROR"
        echo "   expected:              $SAD_FB_EXPECTED_OUTPUT"
        echo "   but the output was:    $SAD_FB_OUTPUT"
    fi
done
