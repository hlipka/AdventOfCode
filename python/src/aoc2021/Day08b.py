class Data:
    def __init__(self, line):
        data = line.split('|')
        self.in_patterns = data[0].strip().split(' ')  # list of the distinct input patterns
        self.display_patterns = data[1].strip().split(' ')  # the four outputs
        self.wires_to_digits = {}  # which patterns result in which digit

    def wires_for_length(self, length):
        for p in self.in_patterns:
            if len(p) == length:
                return list(p)

    def all_wires_for_length(self, length):
        wires = []
        for p in self.in_patterns:
            if len(p) == length:
                wires.append(list(p))
        return wires

    def digit(self, digit, pattern):
        pattern.sort()
        self.wires_to_digits[''.join(pattern)] = digit

    @staticmethod
    def get_missing_wires(pattern):
        wires = ['a', 'b', 'c', 'd', 'e', 'f', 'g']
        for p in pattern:
            wires.remove(p)
        return wires

    def get_all_missing_wires(self, patterns):
        wires = []
        for p in patterns:
            wires.extend(self.get_missing_wires(p))
        return wires

    @staticmethod
    def pattern_with_all_wires(patterns, wires):
        for p in patterns:
            if all(w in p for w in wires):
                return p
        return None

    def get_number(self, pattern):
        k = ''.join(sorted(pattern))
        return self.wires_to_digits[k]

    def map_wires(self):
        # get the four patterns we already know
        one = self.wires_for_length(2)
        self.digit(1, one)
        seven = self.wires_for_length(3)
        self.digit(7, seven)
        four = self.wires_for_length(4)
        self.digit(4, four)
        eight = self.wires_for_length(7)
        self.digit(8, eight)

        # look at the patterns with 6 segments
        p6 = self.all_wires_for_length(6)
        # there are three wires in there which are missing each of the inputs (which would map to the segments c, d, e)
        w = self.get_all_missing_wires(p6)
        # and 'c' is the one of the missing wires which also appears in '1'
        w_c = list(set(one).intersection(set(w)))[0]
        # the pattern with the missing 'c' is then 6
        six = list(filter(lambda p: w_c not in p, p6))[0]
        self.digit(6, six)

        # of the remaining two (missing) wires, one is in 4 ('d')
        w.remove(w_c)
        w_d = list(set(four).intersection(set(w)))[0]
        # the pattern where this is missing is then 0
        zero = list(filter(lambda p: w_d not in p, p6))[0]
        self.digit(0, zero)

        # of the 6-segment patterns the last one is then 9
        p6.remove(zero)
        p6.remove(six)
        nine = p6[0]
        self.digit(9, nine)

        # now look at the 5-segment patterns
        p5 = self.all_wires_for_length(5)
        three = self.pattern_with_all_wires(p5, one)
        self.digit(3, three)
        p5.remove(three)
        # of the remaining patterns, the one with the wire for 'c' must be 2
        two = list(filter(lambda p: w_c in p, p5))[0]
        self.digit(2, two)

        # and the last one is then 5
        p5.remove(two)
        five = p5[0]
        self.digit(5, five)


def run(f_name):
    fin = open(f_name)
    data = []
    summe = 0
    for line in fin:
        d = Data(line.strip())
        d.map_wires()
        data.append(d)
    for dat in data:
        number = 1000 * dat.get_number(dat.display_patterns[0]) + 100 * dat.get_number(
            dat.display_patterns[1]) + 10 * dat.get_number(dat.display_patterns[2]) + dat.get_number(
            dat.display_patterns[3])
        summe = summe + number

    print(summe)


if __name__ == '__main__':
    run('../data/day08.txt')
