import sys
import json
filename=sys.argv[1]
f=open(filename,'r')

sent={}
line_number=0
corpus={}
last_sent=''
i=1
for line in f:
    line=line.strip()
    if line:
        line_number+=1
        if line_number==3:
            A=line.lower().split()
        elif line_number==1:
            InputSentence=line.lower()
        elif line_number==4:
            Qtype=line.lower()
        else:
            Q=line.lower().split()
    else:
        if line_number!=0:
            assert line_number==4
            if last_sent!=InputSentence:
                print InputSentence
                i+=1
            last_sent=InputSentence
            line_number=0
