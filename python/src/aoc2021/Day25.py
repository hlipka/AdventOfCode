import numpy as np

FREE = 0
S = 1
E = 2


def parse_pos(c):
    if c == '.':
        return FREE
    if c == '>':
        return E
    if c == 'v':
        return S


def parse_line(line, lines):
    lines.append(list(map(lambda c: parse_pos(c), list(line))))


def run(f_name):
    lines = []
    fin = open(f_name)
    for line in fin:
        if line.strip() != "":
            parse_line(line.strip(), lines)

    # create a numpy array
    a_map = np.asarray(lines, dtype=np.int8)

    steps = 0
    rows, columns = a_map.shape
    print(rows, columns)
    print(a_map)  # row, column
    while True:
        did_change = False
        # first, all EAST-facing cucumbers move
        next_map1 = np.zeros(a_map.shape, dtype=np.int8)
        for row in range(0, rows):
            for col in range(0, columns):
                c = a_map[row, col]
                if c == E:
                    if a_map[row, (col + 1) % columns] == FREE:  # wrap around
                        next_map1[row, (col + 1) % columns] = E
                        next_map1[row, col] = FREE
                        did_change = True
                    else:
                        next_map1[row, col] = E
                elif c == S:  # we do not write FREE - it might overwrite already moved cucumbers
                    next_map1[row, col] = c
        # then all SOUTH-facing cucumbers move, using the new state
        next_map2 = np.zeros(a_map.shape, dtype=np.int8)
        for row in range(0, rows):
            for col in range(0, columns):
                c = next_map1[row, col]
                if c == S:
                    if next_map1[(row + 1) % rows, col] == FREE:
                        next_map2[(row + 1) % rows, col] = S
                        next_map2[row, col] = FREE
                        did_change = True
                    else:
                        next_map2[row, col] = S
                elif c == E:
                    next_map2[row, col] = c
        a_map = next_map2
        steps += 1
        print(steps)
        print(a_map)
        if not did_change:
            print(steps)
            break


if __name__ == '__main__':
    run('../data/day25.txt')
