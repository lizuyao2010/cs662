OUT="Broken-origInputOnlySent.txt.out"
JSON="$OUT.json"
GOLD="newInput-breakQtype_total.txt"
python readin.py $OUT
python evaluate.py $JSON $GOLD