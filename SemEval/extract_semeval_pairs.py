import os

taketop = .5

ANSWERS_SCALED_DIR = 'platinum/Phase2AnswersScaled'

#w = open('semeval.pairs', 'w')

set_to_def = {}
for line in open('subcategories-definitions.txt'):
    fields = line.split(',')
    num = fields[0].strip()
    let = fields[1].strip()
    defn = ','.join(fields[3:]).strip()
    set_to_def[num+let] = defn
    

for basename in os.listdir(ANSWERS_SCALED_DIR):
    f = os.path.join(ANSWERS_SCALED_DIR, basename)
    numlet = basename.split('-')[1].split('.')[0]
    lines = [line.split('"')[1] for line in open(f) if not line.startswith('#')]
    print ':', set_to_def[numlet]
    for line in lines[:int(len(lines)*taketop)]:
        print ' '.join(line.split(':'))
