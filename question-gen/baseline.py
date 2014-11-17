import urllib2
import sys
def endposition(i,sent):
    for j in range(i,len(sent)):
        row=sent[j]
        for tag in row[14:]:
            if tag=="AM-TMP" or tag=="AM-LOC" or tag=="AM-ADV":
                return j
    return j+1
def inputsentence_analysis(inputsentence):
    post_data = "sentence="+inputsentence
    content=urllib2.urlopen("http://barbar.cs.lth.se:8081/parse",data=post_data).read()
    print "Table:"
    print content
    content=content.split('\n')
    sent=[]
    for row in content:
        table=row.split('\t')
        sent.append(table)
    who_question=[]
    who_answer=[]
    for row in sent:
        if row[14]=="A0":
            who_answer=who_question
            who_answer.append(row[1])
            who_question=[]
            who_question.append("Who")
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
            what_question.insert(0,"What")
            j=endposition(i+1,sent)
            what_answer=[word[1] for word in sent[i:j]]
            break
        else:
            what_question.append(row[1])
    when_question=""
    when_answer=[]
    for (i,row) in enumerate(sent):
        for tag in row[14:]:
            if tag=="AM-TMP":
                when_question="When was this event"
                j=endposition(i+1,sent)
                when_answer=[word[1] for word in sent[i:j]]
                break
    where_question=""
    where_answer=[]
    for (i,row) in enumerate(sent):
        for tag in row[14:]:
            if tag=="AM-LOC":
                where_question="Where was this event"
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
                top=what_question.pop()
                whom_question.insert(0,top)
                whom_question.append(row[1])
                flag=True
                continue
            else:
                whom_question.insert(0,"do")
            whom_question.append(row[2])
            flag=True
        elif row[14]=="AM-ADV":
            whom_question.insert(0,"Whom")
            whom_question.append(row[1])
            j=endposition(i+1,sent)
            whom_answer=[word[1] for word in sent[i+1:j]]
            break
        else:
            whom_question.append(row[1])

    print "Input sentence:"
    print inputsentence
    print "Questions and anwers:"
    print "Q:",' '.join(who_question)
    print "A:",' '.join(who_answer)
    print "Q:",' '.join(what_question)
    print "A:",' '.join(what_answer)
    print "Q:",when_question
    print "A:",' '.join(when_answer)
    print "Q:",where_question
    print "A:",' '.join(where_answer)
    print "Q:",' '.join(whom_question)
    print "A:",' '.join(whom_answer)

if __name__=="__main__":
    for line in sys.stdin:
        if line[0]=="#":
            continue
        inputsentence_analysis(line)