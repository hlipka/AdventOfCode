# let's try to speed up day15b, so it runs in a reasonable time
import datetime
# we can skip the manual sort with this libray
from sortedcontainers import SortedKeyList


# observation: with removing / re-adding values this get slower over time
# probably because we get more distinct keys then
# but it still is about 10 times as fast as using a list and sorting it (day15b)
# and 3 times as fast as using list (for sort) + set (for lookup)

# 1400 seconds seems OK-ish


class Node:
    def __init__(self, value):
        self.value = value
        self.pre = None
        self.length = 10000000
        self.x = 0
        self.y = 0
        self.path = False

    def __repr__(self):
        if self.path:
            return "(%d)" % self.value
        else:
            return " %d " % self.value

    def __lt__(self, other):
        return self.length < other.length

    def __le__(self, other):
        return self.length <= other.length


def parse_line(line):
    result = list(map(lambda c: Node(int(c) - int(0)), list(line)))
    return result


# implement Dijkstra's algorithm
def find_path(map, open_list, goal_x, goal_y):
    map[0][0].length = 0
    map[0][0].value = 0
    start = datetime.datetime.now()
    count = 0
    while len(open_list) > 0:
        if 0 == count % 100:
            now = datetime.datetime.now()
            delta = (now - start).total_seconds()
            print(count, int(delta), int(count / delta), len(open_list))
        count = count + 1

        n = open_list.pop(0)
        if n.x == goal_x and n.y == goal_y:
            print("finished ", n.length)
            return

        nbs = []
        if n.x > 0:
            nbs.append(map[n.y][n.x - 1])
        if n.x < goal_x:
            nbs.append(map[n.y][n.x + 1])
        if n.y > 0:
            nbs.append(map[n.y - 1][n.x])
        if n.y < goal_y:
            nbs.append(map[n.y + 1][n.x])

        for nb in nbs:
            try:
                pos = open_list.index(nb)  # throws ValueError when not found
                alt = n.length + nb.value
                if alt < nb.length:
                    del open_list[pos]  # remove so we can re-add with new values
                    nb.length = alt
                    nb.pre = n
                    open_list.add(nb)
            except ValueError:
                pass
    print("queue finished without visiting end node")


def copy_cave_row(row, ofs):
    result = []
    for n in row:
        value = n.value + ofs
        if value > 9:
            value = value - 9
        result.append(Node(value))
    return result


def copy_cave(map, ofs):
    result = []
    for row in map:
        arow = []
        for n in row:
            value = n.value + ofs
            if value > 9:
                value = value - 9
            arow.append(Node(value))
        result.append(arow)
    return result


def run(f_name):
    fin = open(f_name)
    map = []
    open_list = SortedKeyList(key=lambda n: n.length)
    for line in fin:
        if line.strip() != "":
            row = parse_line(line.strip())
            full_row = row.copy()
            full_row.extend(copy_cave_row(row, 1))
            full_row.extend(copy_cave_row(row, 2))
            full_row.extend(copy_cave_row(row, 3))
            full_row.extend(copy_cave_row(row, 4))
            map.append(full_row)

    full_map = map.copy()
    full_map.extend(copy_cave(map, 1))
    full_map.extend(copy_cave(map, 2))
    full_map.extend(copy_cave(map, 3))
    full_map.extend(copy_cave(map, 4))

    max_x = len(full_map[0])
    max_y = len(full_map)
    for x in range(0, max_x):
        for y in range(0, max_y):
            full_map[y][x].x = x
            full_map[y][x].y = y
            open_list.add(full_map[x][y])

    find_path(full_map, open_list, max_x - 1, max_y - 1)


if __name__ == '__main__':
    run('../../../data/2021/day15.txt')
