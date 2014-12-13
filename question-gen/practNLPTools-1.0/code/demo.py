#!/usr/bin/env python
from practnlptools.tools import Annotator
import sys
annotator=Annotator()

if __name__=='__main__':
    for line in sys.stdin:
        if line[0]=="#":
            continue
        line=line.strip()
        annotations=annotator.getAnnotations(line)
        print annotations['srl']