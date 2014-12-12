import json,sys
filename=sys.argv[1]
with open(filename, 'r') as fp:
    corpus=json.load(fp)

for instance in corpus:
    print instance[u'utterance'].encode('utf-8')