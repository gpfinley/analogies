# Add word frequencies to a CSV file

import sys

incsv, vocabtxt, outcsv = sys.argv[1:4]
w = open(outcsv, 'w')

v = {l.split()[0].strip():l.split()[1].strip() for l in open(vocabtxt)}

for line in open(incsv):
    line = line.strip()
    w.write(line)
    analogy = line.split(',')[0]
    if not ':' in analogy:
        w.write(',freq1,freq2,freq3,freq4')
    else:
        w.write(',')
        w.write(v[analogy.split(':')[0]])
        w.write(',')
        w.write(v[analogy.split(':')[1]])
        w.write(',')
        w.write(v[analogy.split(':')[-2]])
        w.write(',')
        w.write(v[analogy.split(':')[-1]])
    w.write('\n')
w.close()
