import numpy as np


def parse_line(line, amap):
    amap.append(list(map(lambda x: int(x), list(line))))


def is_low(sl):
    center = sl[1, 1]
    return center < sl[0, 0] and center < sl[0, 1] and center < sl[0, 2] and center < sl[1, 0] and center < sl[
        1, 2] and center < sl[2, 0] and center < sl[2, 1] and center < sl[2, 2]


def run(f_name):
    amap = []
    fin = open(f_name)
    for line in fin:
        if line.strip() != "":
            parse_line(line.strip(), amap)

    # create a numpy array
    amap = np.asarray(amap)
    rows, cols = amap.shape
    # and pad it with a high enough value
    amap = np.pad(amap, 1, mode='constant', constant_values=(10))
    score = 0
    # we now can just at all positions and check for a low point
    for row in range(0, rows):
        for col in range(0, cols):
            sl = amap[row:row + 3, col:col + 3]
            if is_low(sl):
                score = score + sl[1, 1] + 1
    print(score)


if __name__ == '__main__':
    run('../../../data/2021/day09.txt')
