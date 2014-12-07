#!/usr/bin/python
# -*- coding: utf-8 -*-
import json
import urllib2
import sys
from nltk.align.bleu import BLEU
from pprint import pprint
import xml.etree.ElementTree as ET
from alchemyapi import AlchemyAPI

stat_question_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0]}
stat_answer_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0]}
weights = [0.25, 0.25, 0.25, 0.25]

def endposition(i,sent):
    j=i
    for j in range(i,len(sent)):
        row=sent[j]
        for tag in row[14:]:
            if tag=="AM-TMP" or tag=="AM-LOC" or tag=="AM-ADV":
                return j
    return j+1
def generate_which(inputsentence,sent):
    which=[]
    typeoftext={}
    alchemyapi = AlchemyAPI()
    response = alchemyapi.entities('text', inputsentence)
    if response['status'] == 'OK':
        for entity in response['entities']:
            typeoftext[entity['text'].encode('utf-8')]=entity['type']
    else:
        print('Error in entity extraction call: ', response['statusInfo'])
    for key in typeoftext:
        which_question=[]
        which_answer=[]
        for (i,row) in enumerate(sent):
            if row[11]=="ROOT":
                if row[3]=="be":
                    which_question.insert(0,row[1])
                    if sent[i+1][5]=="VBG":
                        which_question.append(sent[i+1][1])
                        i+=1
                else:         
                    if row[5]=="VBZ":
                        which_question.insert(0,"does")
                    elif row[5]=="VBD":
                        which_question.insert(0,"did")
                    elif row[5]=="VBG":
                        if which_question:
                            top=which_question.pop()
                            which_question.insert(0,top)
                            which_question.append(row[1])
                            flag=True
                            continue
                    else:
                        which_question.insert(0,"do")
                    which_question.append(row[2])
            elif row[1] == key:
                which_question.insert(0,typeoftext[row[1]])
                which_question.insert(0,"which")
                which_answer=[row[1]]
                which.append({'Q':which_question,'A':which_answer})
                break
            else:
                which_question.append(row[1]) 
    return which

def generate_what(sent):
    what_question=[]
    what_answer=[]
    for (i,row) in enumerate(sent):
        if row[11]=="ROOT":
            if row[3]=="be":
                what_question.insert(0,row[1])
                if sent[i+1][5]=="VBG":
                    what_question.append(sent[i+1][1])
                    i+=1
            else:
                if row[5]=="VBZ":
                    what_question.insert(0,"does")
                elif row[5]=="VBD":
                    what_question.insert(0,"did")
                elif row[5]=="VBG":
                    if what_question:
                        top=what_question.pop()
                        what_question.insert(0,top)
                        what_question.append(row[1])
                        flag=True
                        continue
                else:
                    what_question.insert(0,"do")
                what_question.append(row[2])
            what_question.insert(0,"what")
            what_answer=[word[1] for word in sent[i+1:]]
            break
        else:
            what_question.append(row[1])
    return what_question,what_answer    

def inputsentence_analysis(inputsentence):
    post_data = "sentence="+inputsentence
    content=urllib2.urlopen("http://barbar.cs.lth.se:8081/parse",data=post_data).read()
    #print "Table:"
    #print content
    content=content.split('\n')
    sent=[]
    for row in content:
        table=row.split('\t')
        sent.append(table)
    if len(sent[0])<15:
        print 'semantic parsing failed'
        return {}
    who_question=[]
    who_answer=[]

    for row in sent:
        if row[11]=="ROOT":
            who_answer=who_question
            who_question=[]
            who_question.append("who")
            if row[3]=="be":
                who_question.append(row[1])
            else:    
                who_question.append(row[1])
        else:    
            who_question.append(row[1])

    what_question,what_answer=generate_what(sent)
    '''
    what_question=[]
    what_answer=[]
    flag=False
    for (i,row) in enumerate(sent):
        if row[12]=="Y" and flag==False:
            if row[5]=="VBZ":
                what_question.insert(0,"does")
            elif row[5]=="VBD":
                what_question.insert(0,"did")
            elif row[5]=="VBG":
                if what_question:
                    top=what_question.pop()
                    what_question.insert(0,top)
                    what_question.append(row[1])
                    flag=True
                    continue
            else:
                what_question.insert(0,"do")
            what_question.append(row[2])
            flag=True
        elif row[14]=="A1":
            what_question.insert(0,"what")
            #deal with a
            if sent[i-1][9]==row[0]:
                a=what_question.pop()
                what_answer.append(a)
            j=endposition(i+1,sent)
            what_answer=what_answer+[word[1] for word in sent[i:j]]
            break
        else:
            what_question.append(row[1])
    '''
    '''
    which=[]
    typeoftext={}
    alchemyapi = AlchemyAPI()
    response = alchemyapi.entities('text', inputsentence)
    if response['status'] == 'OK':
        for entity in response['entities']:
            typeoftext[entity['text'].encode('utf-8')]=entity['type']
    else:
        print('Error in entity extraction call: ', response['statusInfo'])
    for key in typeoftext:
        which_question=[]
        which_answer=[]
        flag=False
        for (i,row) in enumerate(sent):
            if row[12]=="Y" and flag==False:
                if row[5]=="VBZ":
                    which_question.insert(0,"does")
                elif row[5]=="VBD":
                    which_question.insert(0,"did")
                elif row[5]=="VBG":
                    if which_question:
                        top=which_question.pop()
                        which_question.insert(0,top)
                        which_question.append(row[1])
                        flag=True
                        continue
                else:
                    which_question.insert(0,"do")
                which_question.append(row[2])
                flag=True
            elif row[1] == key:
                which_question.insert(0,typeoftext[row[1]])
                which_question.insert(0,"which")
                which_answer=[row[1]]
                which.append({'Q':which_question,'A':which_answer})
                break
            else:
                which_question.append(row[1])        
    '''
    which=generate_which(inputsentence,sent)
    when_question=[]
    when_answer=[]
    for (i,row) in enumerate(sent):
        for tag in row[14:]:
            if tag=="AM-TMP" or tag=="AM-MNR":
                when_question="when was this event".split()
                j=endposition(i+1,sent)
                when_answer=[word[1] for word in sent[i:j]]
                break
    where_question=[]
    where_answer=[]
    for (i,row) in enumerate(sent):
        for tag in row[14:]:
            if tag=="AM-LOC":
                where_question="where was this event".split()
                j=endposition(i+1,sent)
                where_answer=[word[1] for word in sent[i:j]]
                break

    whom_question=[]
    whom_answer=[]
    flag=False
    # need name-en to detect whom or what
    for (i,row) in enumerate(sent):
        if row[12]=="Y" and flag==False:
            if row[5]=="VBZ":
                whom_question.insert(0,"does")
            elif row[5]=="VBD":
                whom_question.insert(0,"did")
            elif row[5]=="VBG":
                if whom_question:
                    top=whom_question.pop()
                    whom_question.insert(0,top)
                    whom_question.append(row[1])
                    flag=True
                    continue
            else:
                whom_question.insert(0,"do")
            whom_question.append(row[2])
            flag=True
        elif row[14]=="AM-ADV":
            whom_question.insert(0,"whom")
            whom_question.append(row[1])
            j=endposition(i+1,sent)
            whom_answer=[word[1] for word in sent[i+1:j]]
            break
        else:
            whom_question.append(row[1])
    
    print "Input sentence:"
    print inputsentence
    print "Questions and anwers:"
    print "Who Q:",' '.join(who_question)
    print "who A:",' '.join(who_answer)
    print "what Q:",' '.join(what_question)
    print "what A:",' '.join(what_answer)
    for item in which:
        which_question=item['Q']
        which_answer=item['A']
        print "which Q:",' '.join(which_question)
        print "which A:",' '.join(which_answer)
    print "when Q:",' '.join(when_question)
    print "when A:",' '.join(when_answer)
    print "where Q:",' '.join(where_question)
    print "where A:",' '.join(where_answer)
    print "whom Q:",' '.join(whom_question)
    print "whom A:",' '.join(whom_answer)
    
    result={}
    result['who']=[{'Q':who_question,'A':who_answer}]
    result['what']=[{'Q':what_question,'A':what_answer}]
    result['when']=[{'Q':when_question,'A':when_answer}]
    result['where']=[{'Q':where_question,'A':where_answer}]
    result['whom']=[{'Q':whom_question,'A':whom_answer}]
    result['which']=which
    return result

def compare_each(my_Q,Q,Qtype): 
    '''   
    print Q
    print my_Q
    '''    
    #number_correct = len(my_Q) * BLEU.modified_precision(my_Q,[Q],n=1)
    number_correct=BLEU.compute(my_Q,[Q],weights)
    #stat_question_acc[Qtype][0]+=len(my_Q)
    stat_question_acc[Qtype][0]+=1
    stat_question_acc[Qtype][1]+=number_correct 

def compare(my_answer,sent):
    Qtype=sent['Qtype'].lower()
    if Qtype not in stat_question_acc.keys():
        print Qtype,'not in my set'
        return
    Q=sent['Q'].lower().split()
    #A=sent['A'].lower().split()
    for item in my_answer[Qtype]:
        print item
        if item['Q']:
            compare_each(item['Q'],Q,Qtype)
    
    

def evaluate():
    filename=sys.argv[1]
    json_data=open(filename)
    data=json.load(json_data)
    data=data[0:3]
    for story in data:
        if story:
            for sent in story:
                if sent:
                    if sent['InputSentence']:
                        InputSentence=sent['InputSentence']
                        print InputSentence
                        my_answer=inputsentence_analysis(InputSentence)
                        if my_answer:
                            compare(my_answer,sent)
    json_data.close()
  

def evaluate2():
    filename=sys.argv[1]
    tree = ET.parse(filename)
    root = tree.getroot()
    for instance in root.findall('instance'):
        inputsentence=unicode(instance.find('text').text.strip()).encode('utf-8')
        for question in instance.findall('question'):
            Qtype=question.get('type').strip()
            question=question.text.strip().rstrip('?')
            sent={}
            sent['Qtype']=Qtype
            sent['Q']=question
            sent['InputSentence']=inputsentence
            my_answer=inputsentence_analysis(inputsentence)
            if my_answer:
                compare(my_answer,sent)

def evaluate3():
    filename=sys.argv[1]
    f=open(filename,'r')
    sent={}
    line_number=0
    for line in f:
        line=line.strip()
        if line:
            line_number+=1
            if line_number==3:
                sent['A']=line.lower()
            elif line_number==1:
                sent['InputSentence']=line.lower()
            elif line_number==4:
                sent['Qtype']=line.lower()
            else:
                sent['Q']=line.lower()
        else:
            if line_number!=0:
                assert line_number==4
                my_answer=inputsentence_analysis(sent['InputSentence'])
                if my_answer:
                    compare(my_answer,sent)
            line_number=0

def demo():
    for line in sys.stdin:
        if line[0]=="#":
            continue
        inputsentence_analysis(line)

if __name__=="__main__":
    #demo()
    #evaluate()
    #evaluate2()
    evaluate3()
    for key in stat_question_acc:
        if stat_question_acc[key][0]>0:
            print key, 'question acc:', float(stat_question_acc[key][1])/stat_question_acc[key][0]

    for key in stat_answer_acc:
        if stat_answer_acc[key][0]>0:
            print key, 'answr acc:', float(stat_answer_acc[key][1])/stat_answer_acc[key][0]  






