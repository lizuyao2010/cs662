from nltk.corpus import wordnet as wn
import sys
word=sys.argv[1]


for synset in wn.synsets(word,pos='n')[0].hypernyms():
    hypers=synset.name()
    hypers=hypers.split('.')
    if hypers[1]=='n':
        print hypers[0]