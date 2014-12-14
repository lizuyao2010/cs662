import sys
from nltk.align.bleu import BLEU
import json

stat_question_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0],'how':[0,0],'why':[0,0],'how long':[0,0]}
stat_answer_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0],'how':[0,0],'why':[0,0],'how long':[0,0]}
weights = [0.25, 0.25, 0.25, 0.25]

def compare_each(my_Q,Q,Qtype):   
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
    if my_answer.has_key(Qtype):
        for item in my_answer[Qtype]:
            #print item
            compare_each(item['Q'],Q,Qtype)

f1=sys.argv[1]
with open(f1, 'rb') as fp:
    corpus = json.load(fp)
filename=sys.argv[2]
f=open(filename,'r')
sent={}
last_sent=''
line_number=0
i=1
for line in f:
    line=line.strip()
    if line:
        line_number+=1
        if line_number==3:
            sent['A']=line.lower()
        elif line_number==1:
            sent['InputSentence']=line.lower().rstrip('.')
        elif line_number==4:
            sent['Qtype']=line.lower()
        else:
            sent['Q']=line.lower()
    else:
        if line_number!=0:
            assert line_number==4
            if last_sent!=sent['InputSentence']:
                if corpus.has_key(str(i)):
                    my_answer=corpus[str(i)]
                    if my_answer:
                        compare(my_answer,sent)
                else:
                    print 'corpus has no key:', i
                i+=1
            
            line_number=0

for key in stat_question_acc:
    if stat_question_acc[key][0]>0:
        print key, 'question acc:', float(stat_question_acc[key][1])/stat_question_acc[key][0]

for key in stat_answer_acc:
    if stat_answer_acc[key][0]>0:
        print key, 'answr acc:', float(stat_answer_acc[key][1])/stat_answer_acc[key][0]

print stat_question_acc

