def run(f_name):
    fin = open(f_name)
    line = fin.readline()
    last_num = int(line)
    increase = 0

    for line in fin:
        if line != "":
            num = int(line)
            if num > last_num:
                increase = increase + 1
            last_num = num

    print(increase)


if __name__ == '__main__':
    run('../../../data/2021/day01.txt')
