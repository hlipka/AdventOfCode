# line length
length = 0
total = 0
# counts per bit position
counts = []


# for each line, update the counter for the bit positoon
def parse(line):
    global length
    global counts
    for i in range(0, length):
        c = line[i]
        counts[i] += int(c)


def run(f_name):
    global total
    global length
    global counts

    fin = open(f_name)
    line = fin.readline()

    # remember the length of the lines
    length = len(line) - 1

    # for the first line, set the count for each bit position
    for i in range(0, length):
        counts.append(int(line[i]))

    for line in fin:
        total = total + 1
        if line != "":
            parse(line)

    half = total / 2
    gamma = '0b'
    epsilon = '0b'
    # create new binary numbers from the bit counts
    for i in range(0, length):
        if counts[i] > half:
            gamma = gamma + '1'
            epsilon = epsilon + '0'
        else:
            gamma = gamma + '0'
            epsilon = epsilon + '1'
    print(gamma)
    print(int(gamma, 0))
    print(epsilon)
    print(int(epsilon, 0))
    print(int(gamma, 0) * int(epsilon, 0))


if __name__ == '__main__':
    run('../../../data/2021/day03.txt')
