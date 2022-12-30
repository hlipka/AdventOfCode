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


def has_no_duplicate_smalls(path):
    smalls = []
    for c in path:
        if not c.is_big():
            if c in smalls:
                return False
            smalls.append(c)
    return True


# similar to part 1
def visit_paths(path, next_cave, paths):
    if next_cave.is_start():
        return
    if next_cave.is_end():
        path.append(next_cave)
        paths.append(path)
        return
    if next_cave.is_big():
        do_visit_path(path, next_cave, paths)
    else:
        # when the small cave has been seen before, check that the no other cave was visited twice already
        if next_cave not in path:
            do_visit_path(path, next_cave, paths)
        elif has_no_duplicate_smalls(path):
            do_visit_path(path, next_cave, paths)


def do_visit_path(path, next_cave, paths):
    path.append(next_cave)
    for t in next_cave.targets:
        visit_paths(list(path), t, paths)


def run(f_name):
    caves = {}
    fin = open(f_name)
    for line in fin:
        if len(line.strip()) > 0:
            parse_line(line.strip(), caves)

    start = caves['start']
    paths = []

    do_visit_path([], start, paths)

    print(paths)
    print(len(paths))


if __name__ == '__main__':
    run('../../../data/2021/day12.txt')
