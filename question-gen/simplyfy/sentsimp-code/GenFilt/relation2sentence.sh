#!/bin/sh

grep "(1," $1 | cut -d ")" -f1 | sed 's/(1, //g' | perl -nae 'for ($i=0; $i< @F; $i++) { $F[$i] =~ s/\-\d+(.*?)$//g; print "$F[$i] "  }; print "\n"  ' | sed 's/-RRB-/)/g; s/-LRB-/(/g;'
