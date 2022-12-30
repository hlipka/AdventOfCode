# map each char of the line to a bit number
def map_bits(line):
    result = []
    for c in line.rstrip():
        result.append(int(c))
    return result


# counts the number of '1' bits at the given position
def count_bits(lines, pos):
    count = 0
    for line in lines:
        count = count + line[pos]
    return count


# filters the given line for the bit value at the given position
def filter_line(line, pos, value):
    return line[pos] == value


def run(f_name):
    fin = open(f_name)
    lines = list(map(map_bits, fin.readlines()))
    fin.close()

    pos = 0
    while len(lines) != 1:
        ones = count_bits(lines, pos)
        filter_bit = 0
        if ones >= len(lines) - ones:
            filter_bit = 1
        lines = list(filter(lambda l: filter_line(l, pos, filter_bit), lines))
        pos = pos + 1
    print(lines)
    o2 = ''.join(map(lambda b: str(b), lines[0]))
    print(o2)

    fin = open(f_name)
    lines = list(map(map_bits, fin.readlines()))
    fin.close()

    pos = 0
    while len(lines) != 1:
        ones = count_bits(lines, pos)
        filter_bit = 1
        if ones >= len(lines) - ones:
            filter_bit = 0
        lines = list(filter(lambda l: filter_line(l, pos, filter_bit), lines))
        pos = pos + 1
    print(lines)
    co2 = ''.join(map(lambda b: str(b), lines[0]))
    print(co2)
    print(int('0b' + o2, 0) * int('0b' + co2, 0))


if __name__ == '__main__':
    run('../data/day03.txt')
