f=open('newInput-break.txt','r')
f_i=open('newInput_sentence.txt','w')
f_q=open('newInput_question.txt','w')
line_number=0
for line in f:
    line=line.strip()
    if line:
        line_number+=1
        if line_number==3:
            print >> f_i, line
        elif line_number==1:
            print >> f_i, line
        else:
            print >> f_q, line
            print >> f_q, line.split()[0]
    else:
        if line_number!=0:
            assert line_number==3
        line_number=0