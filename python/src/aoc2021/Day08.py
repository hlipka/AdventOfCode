class Data:
    def __init__(self, line):
        data = line.split('|')
        self.in_patterns = data[0].strip().split(' ')
        self.display_patterns = data[1].strip().split(' ')


def run(f_name):
    fin = open(f_name)
    data = []
    count = 0
    for line in fin:
        data.append(Data(line.strip()))
    # just count the needed signals
    for dat in data:
        for dp in dat.display_patterns:
            signal_count = len(dp)
            if signal_count == 2 or signal_count == 3 or signal_count == 4 or signal_count == 7:
                count = count + 1
    print(count)


if __name__ == '__main__':
    run('../../../data/2021/day08.txt')
