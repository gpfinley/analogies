import sys

def write_pairs(active_category, pairs):
    print(active_category.strip())
    for pair in pairs:
        print pair[0], pair[1]


def main(filename):
    f = open(filename)
    NONE=""
    active_category = NONE
    pairs = set()
    for line in f:
        if line.startswith('#'): continue
        if line.startswith(':'):
            if active_category != NONE:
                write_pairs(active_category, pairs)
                pairs = set()
            active_category = line
        else:
            analogy = line.strip().split()
            if len(analogy) == 4:
                pairs.add((analogy[0], analogy[1]))
                pairs.add((analogy[2], analogy[3]))
    write_pairs(active_category, pairs)


if __name__ == "__main__":
    main(sys.argv[1])
