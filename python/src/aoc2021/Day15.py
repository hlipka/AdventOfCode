class Node:
    def __init__(self, value):
        self.value = int(value) - int(0)
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
    result = list(map(lambda c: Node(c), list(line)))
    return result


# implement Dijkstra's algorithm
def find_path(map, open_list, goal_x, goal_y):
    map[0][0].length = 0
    map[0][0].value = 0
    open_list.sort(reverse=True)
    while len(open_list) > 0:
        n = open_list.pop()
        if n.x == goal_x and n.y == goal_y:
            print("finished ", n.length)
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
            if nb in open_list:
                alt = n.length + nb.value
                if alt < nb.length:
                    nb.length = alt
                    nb.pre = n
                    open_list.sort(reverse=True)


def run(f_name):
    fin = open(f_name)
    map = []
    open_list = []
    y = 0
    for line in fin:
        if line.strip() != "":
            row = parse_line(line.strip())
            x = 0
            for n in row:
                open_list.append(n)
                n.x = x
                n.y = y
                x = x + 1
            y = y + 1
            map.append(row)

    find_path(map, open_list, len(map[9]) - 1, y - 1)


if __name__ == '__main__':
    run('../data/day15.txt')
