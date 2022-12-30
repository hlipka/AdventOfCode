def parse_pattern(line):
    p = line.split(' -> ')
    return p[0], p[1]


# instead of working on the chain directly, we know that each replacement consumes the original pattern
# and replaces it with two new patterns
# this is deterministic, so we can do it for all occurrences at once - hence we just store the counts for each pattern
def replace(counts, patterns, char_counts):
    result = dict.fromkeys(counts.keys(), 0)
    for c in counts:
        if counts[c] != 0:
            count = counts[c]
            repl = patterns[c]
            r1 = c[0] + repl
            r2 = repl + c[1]
            add_char_count(char_counts, repl, count)

            result[r1] = result[r1] + count
            result[r2] = result[r2] + count

    return result


def add_char_count(char_counts, c, count):
    if c not in char_counts:
        char_counts[c] = count
    else:
        char_counts[c] += count


def run(f_name):
    fin = open(f_name)
    start = fin.readline().strip()

    patterns = {}
    counts = {}
    char_counts = {}
    for line in fin:
        if line.strip() != "":
            mapping = parse_pattern(line.strip())
            patterns[mapping[0]] = mapping[1]
            counts[mapping[0]] = 0

    # count how often each pattern occurs in the start polymer
    # also count how often each character is in the polymer
    for i in range(0, len(start) - 1):
        p = start[i:i + 2]
        if p in patterns:
            if p in counts:
                counts[p] += 1
            else:
                counts[p] = 1
        add_char_count(char_counts, start[i:i + 1], 1)
    add_char_count(char_counts, start[len(start) - 1:len(start)], 1)

    for r in range(0, 40):
        counts = replace(counts, patterns, char_counts)

    min_c = min(char_counts.values())
    max_c = max(char_counts.values())
    print(max_c - min_c)


if __name__ == '__main__':
    run('../data/day14.txt')
