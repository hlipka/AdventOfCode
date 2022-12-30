from collections import Counter


def parse_pattern(line):
    p = line.split(' -> ')
    return p[0], p[1]


def replace(pattern, patterns):
    result = [pattern[0]]
    # run through the current polymer chain from left to right
    # check each 2-letter combination for a pattern, append to result
    for pos in range(0, len(pattern) - 1):
        p = pattern[pos] + pattern[pos + 1]
        if p in patterns:
            c = patterns[p]
            result.append(c)
        result.append(pattern[pos + 1])
    return result


def run(f_name):
    fin = open(f_name)
    start = fin.readline().strip()

    # parse the replacement patterns
    patterns = {}
    for line in fin:
        if line.strip() != "":
            mapping = parse_pattern(line.strip())
            patterns[mapping[0]] = mapping[1]

    # just run through all replacement rounds
    pattern = list(start)
    for round_number in range(0, 10):
        pattern = replace(pattern, patterns)
        c = Counter(pattern)
        print(c)

    print(pattern)
    print(len(pattern))
    c = Counter(pattern)
    print(c)
    nums = {value: key for key, value in c.items()}
    print(nums)
    min_c = min(nums.keys())
    max_c = max(nums.keys())
    print(max_c - min_c)


if __name__ == '__main__':
    run('../../../data/2021/day14.txt')
