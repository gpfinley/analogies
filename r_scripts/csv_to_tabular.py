import fileinput
import re

"""
Specifically designed for use with CSVs written from R after processing using these scripts.
Not for general use (doesn't actually parse the CSV standard properly)
"""

for line in fileinput.input():
    line = line.strip()
    outln = ''
    for field in line.split(','):
        outln += ' & '
        try:
            n = float(field)
            n = "{:.3f}".format(n)
            if n[0] == '0':
                n = n[1:]
            elif n[0:2] == '-0':
                n = '$-$' + n[2:]
            outln += n
        except:
            outln += field

    outln = re.sub('"', '', outln)
    # line = re.sub(',', ' & ', line)
    outln += ' \\\\'
    outln = outln[outln.find('&')+2:]
    outln = outln[outln.find('&')+2:]
    print outln
