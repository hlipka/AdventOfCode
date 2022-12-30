import re
import numpy as np

# pattern for a line
pattern = r'(\d+),(\d+) -> (\d+),(\d+)'


class Line:
    def __init__(self, line):
        match = re.search(pattern, line)
        self.x_start = int(match.group(1))
        self.y_start = int(match.group(2))
        self.x_end = int(match.group(3))
        self.y_end = int(match.group(4))

    def dump(self):
        print(self.x_start, self.y_start, self.x_end, self.y_end)

    def max_x(self):
        return max(self.x_start, self.x_end)

    def max_y(self):
        return max(self.y_start, self.y_end)

    def draw_line_x(self, amap):
        start = min(self.y_start, self.y_end)
        end = max(self.y_start, self.y_end) + 1
        for i in range(start, end):
            amap[self.x_start][i] = amap[self.x_start][i] + 1

    def draw_line_y(self, amap):
        start = min(self.x_start, self.x_end)
        end = max(self.x_start, self.x_end) + 1
        for i in range(start, end):
            amap[i][self.y_start] = amap[i][self.y_start] + 1

    def draw_line(self, amap):
        if self.x_start == self.x_end:
            self.draw_line_x(amap)
        elif self.y_start == self.y_end:
            self.draw_line_y(amap)
        return


def run(f_name):
    fin = open(f_name)
    amap = np.full((1000, 1000), 0)

    for line in fin:
        line = line.strip()
        if line == '':
            break
        parsed_line = Line(line)
        # l.dump()
        parsed_line.draw_line(amap)
        # print(amap.transpose())

    # create a new array which marks all places crossed more than once
    mark = amap.flat > 1
    # select these elements, and sum them up
    crosses = amap.flat[mark]
    print(len(crosses))


if __name__ == '__main__':
    run('../data/day05.txt')
