def move_crabs(crabs, pos):
    fuel = 0
    for crab in crabs:
        fuel = fuel + abs(crab - pos)
    return fuel


def run(f_name):
    fin = open(f_name)
    crab_line = fin.readline().strip()
    crabs = list(map(lambda f: int(f), crab_line.split(',')))
    print(crabs)
    min_pos = min(crabs)
    max_pos = max(crabs)
    min_fuel = move_crabs(crabs, max_pos)

    # it does not make sense to move outside the lines of crabs, so the range is from min to max
    # for each potential position, move all crabs to it and calculate the needed fuel
    for pos in range(min_pos, max_pos):
        fuel = move_crabs(crabs, pos)
        if fuel < min_fuel:
            print(pos)
            min_fuel = fuel

    print(min_fuel)


if __name__ == '__main__':
    run('../../../data/2021/day07.txt')
