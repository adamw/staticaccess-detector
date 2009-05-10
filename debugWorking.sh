#!/bin/bash
ant
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5007 -jar ../findbugs-1.3.8/lib/findbugs.jar -textui -auxclasspath dist/staticaccess-detector-annotations.jar -pluginList dist/staticaccess-detector.jar test/working/**/Test1.class 