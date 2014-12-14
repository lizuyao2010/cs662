#!/usr/bin/env python
from practnlptools.tools import Annotator
from collections import defaultdict
from nltk.stem import WordNetLemmatizer
from alchemyapi import AlchemyAPI
import sys
annotator=Annotator()
wordnet_lemmatizer = WordNetLemmatizer()
alchemyapi = AlchemyAPI()
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
        if 'AM-MOD' not in srl:
            question.append('was')
        question.append(srl[item])        

def generate_question(srl,questions,role,Qtype):
    question=[Qtype]
    answer=srl[role]
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
                    else:
                        breakverb(item,srl,question)
                else:
                    question.append(srl[item])
    questions[Qtype].append((' '.join(question),answer))

def get_sent(srl):
    s=[]
    for role in full_template:
        if role in srl:
            s.append(srl[role])
    return ' '.join(s)


def get_entities(inputsentence):
    typeoftext={}
    response = alchemyapi.entities('text', inputsentence)
    if response['status'] == 'OK':
        for entity in response['entities']:
            typeoftext[entity['text'].encode('utf-8')]=entity['type']
    else:
        print('Error in entity extraction call: ', response['statusInfo'])
    return typeoftext

def generate_which(srl,inputsentence,questions):
    typeoftext=get_entities(inputsentence)
    annotations=annotator.getAnnotations(inputsentence)
    pos=annotations['pos']
    for key in typeoftext:
        question=[]
        answer=''
        for word in pos:
            if word[0]==key:
                question.insert(0,'what '+typeoftext[word[0]])
                answer=word[0]
            else:
                if word[0]==srl['V']:
                    breakverb('V',srl,question)
                else:
                    question.append(word[0])
        questions['what'].append((' '.join(question),answer))


def generate(srl,questions):
    srl2sent=get_sent(srl)
    generate_which(srl,srl2sent,questions)
    for role in srl:
        if role == 'V' or role=='AM-MOD' or role=='AM-DIS' or role=='AM-ADV'\
         or role=='R-A0' or role=='R-A1' or role=='R-A2' or role=='C-V' or role=='AM-NEG':
            continue
        for Qtype in role2Qtype[role]:
            generate_question(srl,questions,role,Qtype) 

def printQ(questions,line,i):
    for Qtype in questions:
        for question_answer in questions[Qtype]:
            print i,line
            print question_answer[0]
            print question_answer[1]
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
            print 'semantic role labeling failed'
            continue
        print srl
        pos_d=dict(pos)
        questions=defaultdict(list)
        for item in srl:
            generate(item,questions)
        printQ(questions,line,i)