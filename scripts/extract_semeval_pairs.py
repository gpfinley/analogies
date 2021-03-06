import os
import sys

"""
Will extract pairs from SemEval 2012 data.
Provide it two arguments: the path of the SemEval distribution,
and the path to the 'Phase2AnswersScaled' folder in the SemEval platinum standard.
Both are downloadable from https://sites.google.com/site/semeval2012task2/download
"""

semeval_dir, answers_scaled_dir = sys.argv[1:3]

taketop = .5

set_to_def = {}
for line in open(os.path.join(semeval_dir, 'subcategories-definitions.txt')):
    fields = line.split(',')
    num = fields[0].strip()
    let = fields[1].strip()
    defn = ','.join(fields[3:]).strip()
    set_to_def[num+let] = defn
    

for basename in os.listdir(answers_scaled_dir):
    f = os.path.join(answers_scaled_dir, basename)
    numlet = basename.split('-')[1].split('.')[0]
    lines = [line.split('"')[1] for line in open(f) if not line.startswith('#')]
    print ':', set_to_def[numlet]
    for line in lines[:int(len(lines)*taketop)]:
        print ' '.join(line.split(':'))
