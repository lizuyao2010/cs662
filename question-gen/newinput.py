f=open('newInput.txt','r')
fq=open('newTrain_q','w')
fa=open('newTrain_a','w')
for (i,line) in enumerate(f,1):
    line=line.strip()
    if i%4==1:
        print >> fa,line
    elif i%4==2:
        print >> fq,line
        for w in line.split()[1:-1]:
            print >> fq, w
            print >> fa, w
        print >> fq,line.split()[0]
    elif i%4==3:
        print >> fa,line
