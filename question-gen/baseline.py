#!/usr/bin/python
# -*- coding: utf-8 -*-
import json
import urllib2
import sys
from pprint import pprint
stat_question_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0]}
stat_answer_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0]}
def endposition(i,sent):
    j=i
    for j in range(i,len(sent)):
        row=sent[j]
        for tag in row[14:]:
            if tag=="AM-TMP" or tag=="AM-LOC" or tag=="AM-ADV":
                return j
    return j+1

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
        if row[14]=="A0":
            who_answer=who_question
            who_answer.append(row[1])
            who_question=[]
            who_question.append("who")
        elif row[3]=="be":
            who_question.append("is")
        else:    
            who_question.append(row[1])
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
            j=endposition(i+1,sent)
            what_answer=[word[1] for word in sent[i:j]]
            break
        else:
            what_question.append(row[1])
    when_question=[]
    when_answer=[]
    for (i,row) in enumerate(sent):
        for tag in row[14:]:
            if tag=="AM-TMP":
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
    '''
    print "Input sentence:"
    print inputsentence
    print "Questions and anwers:"
    print "Q:",' '.join(who_question)
    print "A:",' '.join(who_answer)
    print "Q:",' '.join(what_question)
    print "A:",' '.join(what_answer)
    print "Q:",' '.join(when_question)
    print "A:",' '.join(when_answer)
    print "Q:",' '.join(where_question)
    print "A:",' '.join(where_answer)
    print "Q:",' '.join(whom_question)
    print "A:",' '.join(whom_answer)
    '''
    return {'who':{'Q':who_question,'A':who_answer},'what':{'Q':what_question,'A':what_answer},\
    'when':{'Q':when_question,'A':when_answer},'where':{'Q':where_question,'A':where_answer},\
    'whom':{'Q':whom_question,'A':whom_answer}}

def compare_wbw(my_Q,Q,Qtype,isQ):
    if isQ:
        for word in my_Q:
            stat_question_acc[Qtype][0]+=1
            if word in Q:
                stat_question_acc[Qtype][1]+=1
    else:
        for word in my_Q:
            stat_answer_acc[Qtype][0]+=1
            if word in Q:
                stat_answer_acc[Qtype][1]+=1


def compare(my_answer,sent):
    Qtype=sent['Qtype'].lower()
    if Qtype not in stat_question_acc.keys():
        print Qtype,'not in my set'
        return
    Q=sent['Q'].lower().split()
    A=sent['A'].lower().split()
    Qtype=Qtype.lower()
    my_Q=my_answer[Qtype]['Q']
    my_A=my_answer[Qtype]['A']
    '''
    print Q
    print A
    print my_Q
    print my_A
    '''
    compare_wbw(my_Q,Q,Qtype,True)
    compare_wbw(my_A,A,Qtype,False)

if __name__=="__main__":
    '''
    for line in sys.stdin:
        if line[0]=="#":
            continue
        inputsentence_analysis(line)
    '''
    json_data=open('InputQA.json')
    data=json.load(json_data)
    #data=data[1:2]
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
    for key in stat_question_acc:
        if stat_question_acc[key][0]>0:
            print key, 'question acc:', float(stat_question_acc[key][1])/stat_question_acc[key][0]

    for key in stat_answer_acc:
        if stat_answer_acc[key][0]>0:
            print key, 'answr acc:', float(stat_answer_acc[key][1])/stat_answer_acc[key][0]








