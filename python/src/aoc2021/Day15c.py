class Node:
    def __init__(self, value):
        self.value = value
        self.pre = None
        self.length = 10000000
        self.x = 0
        self.y = 0
        self.path = False
        self.guess = 1000000

    def __repr__(self):
        if self.path:
            return "(%d)" % self.value
        else:
            return " %d " % self.value

    def __lt__(self, other):
        return self.guess < other.guess

    def __le__(self, other):
        return self.guess <= other.guess


def parse_line(line):
    result = list(map(lambda c: Node(int(c) - int(0)), list(line)))
    return result


def do_guess(x, y, goal_x, goal_y):
    return (goal_x - x) + (goal_y - y)


# implement A* search
def find_path(a_map, goal_x, goal_y):
    start = a_map[0][0]
    start.length = 0
    start.value = 0
    start.guess = do_guess(0, 0, goal_x, goal_y)
    open_set = [start]

    while len(open_set) > 0:
        open_set.sort(reverse=True)
        n = open_set.pop()

        if n.x == goal_x and n.y == goal_y:
            print("finished ", n.length)
            return

        nbs = []
        if n.x > 0:
            nbs.append(a_map[n.y][n.x - 1])
        if n.x < goal_x:
            nbs.append(a_map[n.y][n.x + 1])
        if n.y > 0:
            nbs.append(a_map[n.y - 1][n.x])
        if n.y < goal_y:
            nbs.append(a_map[n.y + 1][n.x])

        for nb in nbs:
            tg = n.length + nb.value
            if tg < nb.length:
                nb.pre = n
                nb.length = tg
                nb.guess = tg + do_guess(nb.x, nb.y, goal_x, goal_y)
                if nb not in open_set:
                    open_set.append(nb)
    print("queue finished without visiting end node")


def copy_cave_row(row, ofs):
    result = []
    for n in row:
        value = n.value + ofs
        if value > 9:
            value = value - 9
        result.append(Node(value))
    return result


def copy_cave(a_map, ofs):
    result = []
    for row in a_map:
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
    a_map = []
    for line in fin:
        if line.strip() != "":
            row = parse_line(line.strip())
            full_row = row.copy()
            full_row.extend(copy_cave_row(row, 1))
            full_row.extend(copy_cave_row(row, 2))
            full_row.extend(copy_cave_row(row, 3))
            full_row.extend(copy_cave_row(row, 4))
            a_map.append(full_row)

    full_map = a_map.copy()
    full_map.extend(copy_cave(a_map, 1))
    full_map.extend(copy_cave(a_map, 2))
    full_map.extend(copy_cave(a_map, 3))
    full_map.extend(copy_cave(a_map, 4))

    max_x = len(full_map[0])
    max_y = len(full_map)
    for x in range(0, max_x):
        for y in range(0, max_y):
            full_map[y][x].x = x
            full_map[y][x].y = y

    find_path(full_map, max_x - 1, max_y - 1)


if __name__ == '__main__':
    run('../data/day15.txt')
