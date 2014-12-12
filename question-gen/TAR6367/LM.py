import sys
from collections import defaultdict

bigram=defaultdict(lambda : -50.0)
unigram=defaultdict(lambda : -50.0)
filename=sys.argv[1]
with open(filename,'r') as f:
    lines=f.readlines()
flag1=False
flag2=False
for (i,line) in enumerate(lines):
    line=line.strip().lower()
    
    if '1-grams' in line:
        flag1=True
        continue

    if '2-grams' in line:
        flag1=False
        flag2=True
        continue

    if '3-grams' in line:
        flag2=False

    if flag1:
        if line:
            line=line.split()
            key=line[1]
            value=float(line[0])
            unigram[key]=value

    if flag2:
        if line:
            line=line.split()
            key=tuple(line[1:3])
            value=float(line[0])
            bigram[key]=value

def calculatescore(question):
    total=0
    sentence=question[:]
    sentence.insert(0,'<s>')
    sentence.append('</s>')
    for (i,word) in enumerate(sentence,0):
        if i+1<len(sentence):
            key=tuple(sentence[i:i+2])
            score=bigram[key]
            total+=score
    #print sentence,-total/len(sentence)
    return -total/len(sentence)

def modify(words):
    if len(words)==1:
        return words
    score=calculatescore(words[:])
    # best is a copy of words
    best=words[:]
    for (i,delete) in enumerate(words,0):
        deleted=words[:i]+words[i+1:]
        newscore = calculatescore(deleted)
        if score > newscore:
            score=newscore
            best=deleted
    return best
    '''
    if best==words:
        return words
    return modify(best)
    '''

def demo():
    for line in sys.stdin:
        line=line.strip()
        if line[0]=="#":
            continue
        best=modify(line.lower().split())
        print best

if __name__=='__main__':
    demo()

