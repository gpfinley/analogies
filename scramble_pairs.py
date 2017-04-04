"""
Scramble up pairs to make a control set for analogies.
"""

import sys
import random

def write_pairs(active_category, pairs):
    indices = range(len(pairs))
    random.shuffle(indices)
    print(active_category.strip() + "_scrambled")
    for i, pair in enumerate(pairs):
        print(pair[0] + ' ' + pairs[indices[i]][1])


def main():
    f = open(sys.argv[1])
#    w = open(sys.argv[1] + '.scrambled', 'w')

    NONE=""

    active_category = NONE

    pairs = []

    for line in f:
        if line.startswith('#'): continue
        if line.startswith(':'):
            if active_category != NONE:
                write_pairs(active_category, pairs)
                pairs = []
            active_category = line
        else:
            pair = line.strip().split()
            if len(pair) == 2:
                pairs.append(line.strip().split())

    write_pairs(active_category, pairs)
#    write_pairs(active_category, pairs, w)
#    w.close()


if __name__ == "__main__":
    main()