import sys,os
d=dict()
dir_path=sys.argv[1]
all_files=os.listdir(dir_path)
for filename in all_files:
    filepath=dir_path+'/'+filename
    if os.path.isfile(filepath):
        cat=filename.strip().split('.')[0]
        with open(filepath,'r') as fp:
            for line in fp:
                word=line.strip()
                d[word]=cat



