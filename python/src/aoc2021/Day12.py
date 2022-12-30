class Cave:
    def __init__(self, name):
        self.name = name
        self.targets = []
        self.big = name.isupper()
        self.start = (name == "start")
        self.end = (name == "end")

    def __repr__(self):
        return self.name

    def is_big(self):
        return self.big

    def is_start(self):
        return self.start

    def is_end(self):
        return self.end

    def add_target(self, cave):
        self.targets.append(cave)


def parse_line(line, caves):
    path = line.split('-')
    name1 = path[0]
    if name1 not in caves:
        cave1 = Cave(name1)
        caves[name1] = cave1
    else:
        cave1 = caves[name1]
    name2 = path[1]
    if name2 not in caves:
        cave2 = Cave(name2)
        caves[name2] = cave2
    else:
        cave2 = caves[name2]
    cave1.add_target(cave2)
    cave2.add_target(cave1)


def visit_paths(path, next_cave, paths):
    # huzzah!
    if next_cave.is_end():
        path.append(next_cave)
        paths.append(path)
        return
    # when we are in a big cave, just continue all possible paths
    if next_cave.is_big():
        path.append(next_cave)
        for t in next_cave.targets:
            visit_paths(list(path), t, paths)  # continue with a copy of the path object
    else:
        # when we would enter a small cave, check that we did not visit it already
        if next_cave not in path:
            path.append(next_cave)
            for t in next_cave.targets:
                visit_paths(list(path), t, paths)


def run(f_name):
    caves = {}  # we map caves from their name to heir data
    fin = open(f_name)
    for line in fin:
        if len(line.strip()) > 0:
            parse_line(line.strip(), caves)

    start = caves['start']
    paths = []

    # start the visit with the first cave and an empty path
    visit_paths([], start, paths)

    print(paths)
    print(len(paths))


if __name__ == '__main__':
    run('../../../data/2021/day12.txt')
