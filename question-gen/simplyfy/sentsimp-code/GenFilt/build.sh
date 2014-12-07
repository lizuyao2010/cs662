#!/bin/sh

#javac -classpath lib/stanford-parser.jar -d ./classes -sourcepath ./src ./src/edu/cmu/cs/lti/relationFilter/*.java
#jar cf Simplifier.jar -C classes .

svn update
ant

