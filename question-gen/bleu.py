from nltk.align.bleu import BLEU

my_sen="i like eating apple and watching basketball games"
my_ref="we likes eats apples but watches volleyball match"
n=4
weight=[1.0/n]*n

sen=my_sen.lower().split()
ref=my_ref.lower().split()

print BLEU.compute(sen,[ref],weight)