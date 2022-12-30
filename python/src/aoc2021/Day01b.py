class SlidingWindow(object):
    def __init__(self, f_name):
        self.fin = open(f_name)
        v1 = int(self.fin.readline())
        v2 = int(self.fin.readline())
        self.buf = [0, v1, v2]

    def next_value(self):
        line = self.fin.readline()
        if line == "":
            return -1
        val = int(line)
        del (self.buf[0])
        self.buf.append(val)
        return sum(self.buf)


def run(f_name):
    w = SlidingWindow(f_name)
    last_num = w.next_value()
    num = 0
    increase = 0
    while num != -1:
        num = w.next_value()
        if num > last_num:
            increase = increase + 1
        last_num = num

    print(increase)


if __name__ == '__main__':
    run('../../../data/2021/day01.txt')
