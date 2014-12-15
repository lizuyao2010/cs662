import sys
import json
filename=sys.argv[1]
f=open(filename,'r')
sent={}
line_number=0
corpus={}
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
            #print InputSentence
            assert line_number==4
            if not corpus.has_key(InputSentence): 
                corpus[InputSentence]={}
            if not corpus[InputSentence].has_key(Qtype):
                corpus[InputSentence][Qtype]=[]
            corpus[InputSentence][Qtype].append({'Q':Q,'A':A})
        line_number=0
with open(filename+'.json', 'wb') as fp:
    json.dump(corpus, fp)