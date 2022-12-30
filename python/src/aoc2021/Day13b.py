import numpy as np


def parse_dots(line):
    co = line.split(',')
    return int(co[0]), int(co[1])


def parse_fold(line):
    fold = line[11:].split('=')
    return fold[0], int(fold[1])


def do_fold_x(dots, line):
    r = dots[..., :line]
    f = dots[..., line + 1:]
    f = np.fliplr(f)
    m = r.__or__(f)
    return m


def do_fold_y(dots, line):
    r = dots[:line, ...]
    f = dots[line + 1:, ...]
    f = np.flipud(f)
    m = r.__or__(f)
    return m


def do_fold(dots, fold_cmd):
    if fold_cmd[0] == 'x':
        return do_fold_x(dots, fold_cmd[1])
    else:
        return do_fold_y(dots, fold_cmd[1])


def run(f_name):
    fin = open(f_name)
    dot_list = []
    fold_list = []
    max_x = 0
    max_y = 0

    for line in fin:
        if line.strip() != "":
            if line.startswith('fold'):
                fold_list.append(parse_fold(line.strip()))
            else:
                dots = parse_dots(line.strip())
                dot_list.append(dots)
                max_x = max(max_x, dots[0])
                max_y = max(max_y, dots[1])

    # swap x- and y-axis to get it visually as in the instructions
    dots = np.zeros((max_y + 1, max_x + 1), dtype=np.int8, order='C')
    for dot in dot_list:
        dots[dot[1], dot[0]] = 1

    # now follow all he fold instructions
    for f in fold_list:
        dots = do_fold(dots, f)

    # print the result to see the letters
    np.set_printoptions(linewidth=200, formatter={'all': lambda x: '*' if x > 0 else '_'})
    print(dots)


if __name__ == '__main__':
    run('../../../data/2021/day13.txt')
