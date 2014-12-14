#!/usr/bin/env python
from practnlptools.tools import Annotator
from collections import defaultdict
from nltk.stem import WordNetLemmatizer
import sys
annotator=Annotator()
wordnet_lemmatizer = WordNetLemmatizer()
template={}
full_template=['AM-MOD','A0','AM-ADV', 'AM-NEG','V','C-V','AM-DIR','A1','A2','A3','A4','AM-PNC','AM-MNR','AM-LOC','AM-TMP','C-A1']
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
role2Qtype['A0']=['who']
role2Qtype['A1']=['what']
role2Qtype['A2']=['whom']
role2Qtype['A3']=['how long']
role2Qtype['A4']=['where']
role2Qtype['AM-MNR']=['how']
role2Qtype['AM-LOC']=['where']
role2Qtype['AM-TMP']=['when']
role2Qtype['AM-PNC']=['why']
role2Qtype['AM-DIR']=['where to/from']
role2Qtype['AM-PRP']=['why']
role2Qtype['AM-CAU']=['why']
role2Qtype['C-A1']=['how']
pos_d={}

def breakverb(item,srl,question):
    if pos_d[srl[item]]!='VBG' and pos_d[srl[item]]!='VBN' and 'AM-MOD' not in srl:
        question.append(wordnet_lemmatizer.lemmatize(srl[item],'v'))
        if pos_d[srl[item]]=='VBD':
            question.insert(1,'did')
        elif pos_d[srl[item]]=='VBZ':
            question.insert(1,'does')
        else:
             question.insert(1,'do')
    else:
        question.append(srl[item])        

def generate_question(srl,questions,role,Qtype):
    question=[Qtype]
    for item in template[role]:
        if item in srl:
            if role=='A0':
                question.append(srl[item])
            else:
                if item=='V':
                    if len(srl[item].split())>1:
                        adverb=srl[item].split()[1]
                        srl[item]=srl[item].split()[0]
                        question.append(adverb)
                        '''
                        elif pos_d[srl[item]]!='VBG' and pos_d[srl[item]]!='VBN' and 'AM-MOD' not in srl:
                            question.append(wordnet_lemmatizer.lemmatize(srl[item],'v'))
                            if pos_d[srl[item]]=='VBD':
                                question.insert(1,'did')
                            elif pos_d[srl[item]]=='VBZ':
                                question.insert(1,'does')
                            else:
                                question.insert(1,'do')
                        '''
                    else:
                        breakverb(item,srl,question)
                else:
                    #if pos_d[srl[item]]=='VBG' or pos_d[srl[item]]=='VBN':
                        #question.insert(1,word_before[srl[item]])
                    question.append(srl[item])
    questions[Qtype].append(' '.join(question))

def generate(srl,questions):
    for role in srl:
        if role == 'V' or role=='AM-MOD' or role=='AM-DIS' or role=='AM-ADV' or role=='R-A0' or role=='R-A1' or role=='R-A2' or role=='C-V' or role=='AM-NEG':
            continue
        for Qtype in role2Qtype[role]:
            generate_question(srl,questions,role,Qtype) 

def printQ(questions,line,i):
    for Qtype in questions:
        for question in questions[Qtype]:
            print i
            print question
            print 'answer'
            print Qtype
            print

if __name__=='__main__':
    for (i,line) in enumerate(sys.stdin,1):
        if line[0]=="#":
            continue
        line=line.strip().rstrip('.').lower()
        annotations=annotator.getAnnotations(line)
        srl=annotations['srl']
        pos=annotations['pos']
        ner=annotations['ner']
        if not srl:
            continue
        #print line,srl,pos,ner
        pos_d=dict(pos)
        questions=defaultdict(list)
        for item in srl:
            generate(item,questions)
        printQ(questions,line,i)