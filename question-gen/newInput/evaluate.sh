OUT="Broken-origInputOnlySent.txt.out"
#OUT="cmuOutput-2-13-2015"
#OUT="OAeval.txt.12.14"
JSON="$OUT.json"
GOLD="newInput-breakQtype_total.txt"
python readin.py $OUT
python evaluate.py $JSON $GOLD