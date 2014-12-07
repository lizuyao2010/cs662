#!/bin/csh -f
#
# Runs the English PCFG parser on one or more files, printing trees only
# usage: ./lexparser.csh fileToparse+
#

#set scriptdir=/afs/cs.cmu.edu/user/nbach/stanford-parser-2007-08-19
set scriptdir=/afs/cs.cmu.edu/user/nbach/stanford-parser-2008-10-19

/afs/cs.cmu.edu/user/nbach/jdk1.6.0_16/bin/java -mx12g -cp "$scriptdir/stanford-parser.jar:" edu.stanford.nlp.parser.lexparser.LexicalizedParser -outputFormat "wordsAndTags,penn,dependencies" -sentences newline $scriptdir/englishFactored.ser.gz $*


#/afs/cs.cmu.edu/user/nbach/jdk1.5.0_05/bin/java -server -mx500m -cp "$scriptdir/stanford-parser.jar:" edu.stanford.nlp.parser.lexparser.LexicalizedParser -tokenized -sentences newline $scriptdir/englishPCFG.ser.gz $*

#/afs/cs.cmu.edu/user/nbach/jdk1.5.0_05/bin/java -server -mx500m -cp "$scriptdir/stanford-parser.jar:" edu.stanford.nlp.parser.lexparser.LexicalizedParser  -sentences newline $scriptdir/englishPCFG.ser.gz $*



