import re
import numpy as np

# pattern for a line
pattern = r'x=(\-?\d+)\.\.(\-?\d+),y=(\-?\d+)\.\.(\-?\d+),z=(\-?\d+)\.\.(\-?\d+)'


class Region:
    def __init__(self, line):
        self.ofs = 50
        if line.startswith('on'):
            line = line[3:]
            self.value = 1
        else:
            line = line[4:]
            self.value = 0
        match = re.search(pattern, line)
        self.x_start = int(match.group(1))
        self.x_end = int(match.group(2))
        self.y_start = int(match.group(3))
        self.y_end = int(match.group(4))
        self.z_start = int(match.group(5))
        self.z_end = int(match.group(6))

    def fill_region(self, world):
        world[
            self.x_start + self.ofs:self.x_end + self.ofs + 1,
            self.y_start + self.ofs:self.y_end + self.ofs + 1,
            self.z_start + self.ofs:self.z_end + self.ofs + 1] = self.value

    def in_range(self):
        return \
            self.x_start >= -50 and self.x_end <= 50 and \
            self.y_start >= -50 and self.y_end <= 50 and \
            self.z_start >= -50 and self.z_end <= 50


def run(f_name):
    fin = open(f_name)
    world = np.zeros((101, 101, 101), dtype=int)

    for line in fin:
        line = line.strip()
        if line == '':
            continue
        region = Region(line)
        if region.in_range():
            region.fill_region(world)

    print(world.shape)

    # create a new array which marks all turned on cubes
    mark = world.flat == 1
    # select these elements, and count them up
    cubes = world.flat[mark]
    print(len(cubes))


if __name__ == '__main__':
    run('../../../data/2021/day22.txt')
