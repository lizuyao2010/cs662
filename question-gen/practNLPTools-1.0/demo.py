#!/usr/bin/env python
from practnlptools.tools import Annotator
from collections import defaultdict
from nltk.stem import WordNetLemmatizer
import sys
annotator=Annotator()
wordnet_lemmatizer = WordNetLemmatizer()
template={}
full_template=['AM-MOD','A0','V','A1','A2','AM-PNC','AM-MNR','AM-LOC','AM-TMP']
'''
template['A0']=['AM-MOD','V','A1','A2','AM-MNR','AM-LOC','AM-TMP']
template['A1']=['AM-MOD','A0','V','A2','AM-MNR','AM-LOC','AM-TMP']
template['A2']=['AM-MOD','A0','V','A1','AM-MNR','AM-LOC','AM-TMP']
template['AM-MNR']=['AM-MOD','A0','V','A1','A2','AM-LOC','AM-TMP']
template['AM-TMP']=['AM-MOD','A0','V','A1','A2','AM-MNR','AM-LOC']
template['AM-LOC']=['AM-MOD','A0','V','A1','A2','AM-MNR','AM-TMP']
'''
for item in full_template:
    copy_template=full_template[:]
    copy_template.remove(item)
    template[item]=copy_template

role2Qtype={}
role2Qtype['A0']=['who','what']
role2Qtype['A1']=['who','what']
role2Qtype['A2']=['whom']
role2Qtype['AM-MNR']=['how']
role2Qtype['AM-LOC']=['where']
role2Qtype['AM-TMP']=['when']
role2Qtype['AM-PNC']=['why']
role2Qtype['AM-DIR']=['where to/from']
role2Qtype['AM-PRP']=['why']
role2Qtype['AM-CAU']=['why']

pos_d={}

def generate_question(srl,questions,role,Qtype):
    question=[Qtype]
    for item in template[role]:
        if item in srl:
            if role=='A0':
                question.append(srl[item])
            else:
                if item=='V' and pos_d[srl[item]]!='VBG' and pos_d[srl[item]]!='VBN' and 'AM-MOD' not in srl:
                    question.append(wordnet_lemmatizer.lemmatize(srl[item],'v'))
                    if pos_d[srl[item]]=='VBD':
                        question.insert(1,'did')
                    elif pos_d[srl[item]]=='VBZ':
                        question.insert(1,'does')
                    else:
                        question.insert(1,'do')
                else:
                    #if pos_d[srl[item]]=='VBG' or pos_d[srl[item]]=='VBN':
                        #question.insert(1,word_before[srl[item]])
                    question.append(srl[item])
    questions[Qtype].append(' '.join(question))

def generate(srl,questions):
    for role in srl:
        if role == 'V' or role=='AM-MOD':
            continue
        for Qtype in role2Qtype[role]:
            generate_question(srl,questions,role,Qtype) 

def printQ(questions,line):
    for Qtype in questions:
        for question in questions[Qtype]:
            print line
            print question
            print Qtype
            print

if __name__=='__main__':
    for line in sys.stdin:
        if line[0]=="#":
            continue
        line=line.strip().rstrip('.').lower()
        annotations=annotator.getAnnotations(line)
        srl=annotations['srl']
        pos=annotations['pos']
        ner=annotations['ner']
        print srl,pos,ner
        pos_d=dict(pos)
        questions=defaultdict(list)
        for item in srl:
            generate(item,questions)
        printQ(questions,line)