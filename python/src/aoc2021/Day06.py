def run(f_name):
    fin = open(f_name)
    fish_line = fin.readline().strip()
    # parse the initial state of the swarm
    fishes = list(map(lambda f: int(f), fish_line.split(',')))

    # each day, look at the state of the fishes, and append new ones at the end
    for day in range(0, 80):
        for i in range(0, len(fishes)):
            if 0 == fishes[i]:
                fishes[i] = 6
                fishes.append(8)
            else:
                fishes[i] = fishes[i] - 1

    print(len(fishes))


if __name__ == '__main__':
    run('../../../data/2021/day06.txt')
