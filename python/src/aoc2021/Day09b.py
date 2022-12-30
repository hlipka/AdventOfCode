import numpy as np


def parse_line(line, amap):
    amap.append(list(map(lambda x: int(x), list(line))))


def is_low(sl):
    center = sl[1, 1]
    return center < sl[0, 0] and center < sl[0, 1] and center < sl[0, 2] and center < sl[1, 0] and center < sl[
        1, 2] and center < sl[2, 0] and center < sl[2, 1] and center < sl[2, 2]


# recursive flood fill in the map
def search_basin(amap, row, col, fields):
    h = amap[row, col]
    if h > 8:
        return
    fields.append(h)
    amap[row, col] = 11
    search_basin(amap, row - 1, col, fields)
    search_basin(amap, row + 1, col, fields)
    search_basin(amap, row, col - 1, fields)
    search_basin(amap, row, col + 1, fields)


def find_basin(amap, row, col):
    fields = []
    search_basin(amap, row, col, fields)
    return len(fields)


def run(f_name):
    amap = []
    fin = open(f_name)
    for line in fin:
        if line.strip() != "":
            parse_line(line.strip(), amap)

    amap = np.asarray(amap)
    rows, cols = amap.shape
    # pad the array at the edges, so we don't need to handle them specifically during low-point search
    amap = np.pad(amap, 1, mode='constant', constant_values=10)
    # search all low points before doing the search for basins
    # (because the search will mark elements of the basins, throwing off the low-point search)
    lows = []
    for row in range(0, rows):
        for col in range(0, cols):
            sl = amap[row:row + 3, col:col + 3]
            if is_low(sl):
                lows.append((row, col))

    basins = []
    for lp in lows:
        row, col = lp
        basins.append(find_basin(amap, row + 1, col + 1))

    basins.sort(reverse=True)
    print(basins[0] * basins[1] * basins[2])


if __name__ == '__main__':
    run('../../../data/2021/day09.txt')
