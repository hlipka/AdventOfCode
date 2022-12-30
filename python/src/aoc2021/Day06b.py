import numpy as np


def run(f_name):
    fin = open(f_name)
    fish_line = fin.readline().strip()
    fish_data = fish_line.split(',')

    # instead of simulating the swarm, we group fishes by their internal timer
    # this array holds the number of fishes for each timer state
    fishes = [0, 0, 0, 0, 0, 0, 0, 0, 0]

    # parse the initial state
    for fd in fish_data:
        days = int(fd)
        fishes[days] = fishes[days] + 1

    print(fishes)

    for day in range(0, 256):
        print(day, sum(fishes))
        new_fish = fishes[0]  # each fishes with timer=0 spawns  new fish
        for i in range(0, 8):
            fishes[i] = fishes[i + 1]  # move fish state 'upwards'
        fishes[8] = new_fish
        fishes[6] = fishes[6] + new_fish  # add the newly spawned fishes

    print(sum(fishes))


if __name__ == '__main__':
    run('../data/day06.txt')
