class Command:
    def __init__(self, direction, dist):
        if direction == 'forward':
            self.fwd = dist
            self.down = 0
        elif direction == 'down':
            self.fwd = 0
            self.down = dist
        elif direction == 'up':
            self.fwd = 0
            self.down = -dist


class Position:
    def __init__(self):
        self.h = 0
        self.d = 0

    def update(self, cmd):
        self.h = self.h + cmd.fwd
        self.d = self.d + cmd.down


def parse(line):
    parts = line.split(' ', 1)
    return Command(parts[0], int(parts[1]))


# simple parse each line as command, and then execute them to update the position
def run(f_name):
    fin = open(f_name)
    pos = Position()
    for line in fin:
        if line != "":
            cmd = parse(line)
            pos.update(cmd)

    print(pos.h * pos.d)


if __name__ == '__main__':
    run('../../../data/2021/day02.txt')
