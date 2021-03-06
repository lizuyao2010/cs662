import sys
from nltk.align.bleu import BLEU
import json

stat_question_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0],'how':[0,0],'why':[0,0],'how long':[0,0]}
stat_question_acc_BLEU={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0],'how':[0,0],'why':[0,0],'how long':[0,0]}
stat_answer_acc={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0],'how':[0,0],'why':[0,0],'how long':[0,0]}
stat_answer_acc_BLEU={'what':[0,0],'when':[0,0],'where':[0,0],'who':[0,0],'whom':[0,0],'which':[0,0],'how':[0,0],'why':[0,0],'how long':[0,0]}
weights = [0.25, 0.25, 0.25, 0.25]
stat_question_acc_overall=[0,0]
stat_answer_acc_overall=[0,0]


def compare_each_answer(my_Q,Q,Qtype):
    acc=BLEU.modified_precision(my_Q,[Q],n=1)
    BLEU_score=BLEU.compute(my_Q,[Q],weights)
    print 'A-------------'
    print 'my_A:', ' '.join(my_Q)
    print 'gold_A:',' '.join(Q)
    print 'Unigram acc:',acc
    print 'BLEU:',BLEU_score
    print '-------------'
    number_correct = len(my_Q) * acc
    number_correct_BLEU=BLEU_score
    stat_answer_acc[Qtype][0]+=len(Q)
    stat_answer_acc_overall[0]+=len(Q)
    stat_answer_acc_BLEU[Qtype][0]+=1
    stat_answer_acc[Qtype][1]+=number_correct
    stat_answer_acc_overall[1]+=number_correct
    stat_answer_acc_BLEU[Qtype][1]+=number_correct_BLEU   

def compare_each_question(my_Q,Q,Qtype):   
    acc=BLEU.modified_precision(my_Q,[Q],n=1)
    BLEU_score=BLEU.compute(my_Q,[Q],weights)
    print 'Q-------------'
    print 'my_Q:', ' '.join(my_Q)
    print 'gold_Q:',' '.join(Q)
    print 'Unigram acc:',acc
    print 'BLEU:',BLEU_score
    print '-------------'
    number_correct = len(my_Q) * acc
    number_correct_BLEU=BLEU_score
    stat_question_acc[Qtype][0]+=len(Q)
    stat_question_acc_overall[0]+=len(Q)
    stat_question_acc_BLEU[Qtype][0]+=1
    stat_question_acc[Qtype][1]+=number_correct
    stat_question_acc_overall[1]+=number_correct
    stat_question_acc_BLEU[Qtype][1]+=number_correct_BLEU  

def compare(my_answer,sent):
    Qtype=sent['Qtype'].lower()
    if Qtype not in stat_question_acc.keys():
        print Qtype,'not in my set'
        return
    Q=sent['Q'].lower().split()
    A=sent['A'].lower().split()
    if my_answer.has_key(Qtype):
        for item in my_answer[Qtype]:
            compare_each_answer(item['A'],A,Qtype)
            compare_each_question(item['Q'],Q,Qtype)

f1=sys.argv[1]
with open(f1, 'rb') as fp:
    corpus = json.load(fp)
filename=sys.argv[2]
f=open(filename,'r')
sent={}
last_sent=''
line_number=0
i=1
missing=0
for line in f:
    line=line.strip()
    if line:
        line_number+=1
        if line_number==3:
            sent['A']=line.lower()
        elif line_number==1:
            sent['InputSentence']=line.lower().rstrip('.').strip()
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
                    missing+=1
                i+=1
            last_sent=sent['InputSentence']
            line_number=0
print '--------'
print 'total:',i
print '--------'
print 'recall:',1-float(missing)/i
print '--------'
print 'question precision:',float(stat_question_acc_overall[1])/stat_question_acc_overall[0]
print 'answer precision:',float(stat_answer_acc_overall[1])/stat_answer_acc_overall[0]
print '--------'
for key in stat_question_acc_BLEU:
    if stat_question_acc_BLEU[key][0]>0:
        print key, 'question acc(BLEU):', float(stat_question_acc_BLEU[key][1])/stat_question_acc_BLEU[key][0]

print '--------'
for key in stat_question_acc:
    if stat_question_acc[key][0]>0:
        print key, 'question acc(unigram):', float(stat_question_acc[key][1])/stat_question_acc[key][0]
print '--------'
for key in stat_answer_acc_BLEU:
    if stat_answer_acc_BLEU[key][0]>0:
        print key, 'anwser acc(BLEU):', float(stat_answer_acc_BLEU[key][1])/stat_answer_acc_BLEU[key][0]
print '--------'
for key in stat_answer_acc:
    if stat_answer_acc[key][0]>0:
        print key, 'anwser acc(unigram):', float(stat_answer_acc[key][1])/stat_answer_acc[key][0]
print '--------'
