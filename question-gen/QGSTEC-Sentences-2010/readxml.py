import xml.etree.ElementTree as ET
f1=open('train_q','w')
f2=open('train_a','w')
tree = ET.parse('DevelopmentData_QuestionsFromSentences.xml')
root = tree.getroot()
for instance in root.findall('instance'):
    inputsentence=unicode(instance.find('text').text.strip()).encode('utf-8')
    Qtype=instance.find('question').get('type').strip()
    #question=instance.find('question').text.strip()
    for question in instance.findall('question'):
        Qtype=question.get('type').strip()
        question=unicode(question.text.strip()).encode('utf-8')
        print >> f2 ,inputsentence 
        print Qtype
        print >> f1, question