import urllib2
def inputsentence_analysis(inputsentence):
    post_data = "sentence="+inputsentence
    content=urllib2.urlopen("http://barbar.cs.lth.se:8081/parse",data=post_data).read()
    print content
    content=content.split('\n')
    sent=[]
    for row in content:
        table=row.split('\t')
        sent.append(table)
    who_question=[]
    for row in sent:
        if row[14]=="A0":
            who_question.append("Who")
        elif row[3]=="be":
            who_question.append("is")
        else:    
            who_question.append(row[1])
    what_question=[]
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
            what_question.insert(0,"what")
            break
        else:
            what_question.append(row[1])
    print "Input sentence:"
    print inputsentence
    print "Questions:"
    print ' '.join(who_question)
    print ' '.join(what_question)

if __name__=="__main__":
    inputsentence_analysis("I am cooking chicken")
