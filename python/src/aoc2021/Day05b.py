import re
import numpy as np

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

    def draw_line_xy(self, amap):
        if self.x_start > self.x_end:
            start_x = self.x_end
            start_y = self.y_end
            end_x = self.x_start
            end_y = self.y_start
        else:
            start_x = self.x_start
            start_y = self.y_start
            end_x = self.x_end
            end_y = self.y_end
        # similar to the first part, but with diagonal lines as well
        if start_y > end_y:
            direction = -1
        else:
            direction = 1
        length = end_x - start_x + 1
        for i in range(0, length):
            amap[start_x + i][start_y + i * direction] = amap[start_x + i][start_y + i * direction] + 1

    def draw_line(self, amap):
        if self.x_start == self.x_end:
            self.draw_line_x(amap)
        elif self.y_start == self.y_end:
            self.draw_line_y(amap)
        else:
            self.draw_line_xy(amap)


def run(f_name):
    fin = open(f_name)
    amap = np.full((1000, 1000), 0)

    for line in fin:
        line = line.strip()
        if line == '':
            break
        parsed_line = Line(line)
        # parsed_line.dump()
        parsed_line.draw_line(amap)
        # print(amap.transpose())

    # print(amap.transpose())
    mark = amap.flat > 1
    crosses = amap.flat[mark]
    print(len(crosses))


if __name__ == '__main__':
    run('../data/day05.txt')
