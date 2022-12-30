# map the PODs to their rooms
def get_pods(world):
    pods = {}
    for r in world:
        pod = world[r]
        if pod != '':
            pods[pod] = r
    return pods


def get_real_data():
    # map rooms to their content (to find occupied places)
    world = {
        # floors are empty
        '1': '', '2': '', '3': '', '4': '', '5': '', '6': '', '7': '', '8': '', '9': '', '10': '', '11': '',
        # rooms are occupied
        'A1': 'D1', 'A2': 'D2', 'B1': 'B1', 'B2': 'A1', 'C1': 'C1', 'C2': 'B2', 'D1': 'C2', 'D2': 'A2',
    }
    # map pods to their rooms
    pods = get_pods(world)
    return pods, world


def get_test_data():
    world = {
        # floors are empty
        '1': '', '2': '', '3': '', '4': '', '5': '', '6': '', '7': '', '8': '', '9': '', '10': '', '11': '',
        # rooms are occupied
        'A1': 'B1', 'A2': 'A1', 'B1': 'C1', 'B2': 'D1', 'C1': 'B2', 'C2': 'C2', 'D1': 'D2', 'D2': 'A2',
    }
    # map pods to their rooms
    pods = get_pods(world)
    return pods, world


# set of rooms, to where one can move from there, and how much it costs
distances = {
    '1': {'A1': 3,
          'B1': 5,
          'C1': 7,
          'D1': 9},
    '2': {'A1': 2,
          'B1': 4,
          'C1': 6,
          'D1': 8},
    '4': {'A1': 2,
          'B1': 2,
          'C1': 4,
          'D1': 6},
    '6': {'A1': 4,
          'B1': 2,
          'C1': 2,
          'D1': 4},
    '8': {'A1': 6,
          'B1': 4,
          'C1': 2,
          'D1': 2},
    '10': {'A1': 8,
           'B1': 6,
           'C1': 4,
           'D1': 2},
    '11': {'A1': 9,
           'B1': 7,
           'C1': 5,
           'D1': 3},
    'A1': {'B1': 4,
           'C1': 6,
           'D1': 8,
           '1': 3, '2': 2, '4': 2, '6': 4, '8': 6, '10': 8, '11': 9},
    'A2': {'B1': 5,
           'C1': 7,
           'D1': 9,
           '1': 4, '2': 3, '4': 3, '6': 5, '8': 8, '10': 9, '11': 10},
    'B1': {'A1': 4,
           'C1': 4,
           'D1': 6,
           '1': 5, '2': 4, '4': 2, '6': 2, '8': 4, '10': 6, '11': 7},
    'B2': {'A1': 5,
           'C1': 5,
           'D1': 7,
           '1': 6, '2': 5, '4': 3, '6': 3, '8': 5, '10': 7, '11': 8},
    'C1': {'A1': 6,
           'B1': 4,
           'D1': 4,
           '1': 7, '2': 6, '4': 4, '6': 2, '8': 2, '10': 4, '11': 5},
    'C2': {'A1': 7,
           'B1': 5,
           'D1': 5,
           '1': 8, '2': 7, '4': 5, '6': 3, '8': 3, '10': 5, '11': 6},
    'D1': {'A1': 8,
           'B1': 6,
           'C1': 4,
           '1': 9, '2': 8, '4': 6, '6': 4, '8': 2, '10': 2, '11': 3},
    'D2': {'A1': 9,
           'B1': 7,
           'C1': 5,
           '1': 10, '2': 9, '4': 7, '6': 5, '8': 3, '10': 3, '11': 4},
}


# add the remaining distances for the slots, we can determine them from the top position
def fix_distances():
    global distances
    for room in distances:
        dists = distances[room]
        if 'A1' in dists:
            d_a = dists['A1']
            dists['A2'] = d_a + 1

        if 'B1' in dists:
            d_b = dists['B1']
            dists['B2'] = d_b + 1

        if 'C1' in dists:
            d_c = dists['C1']
            dists['C2'] = d_c + 1

        if 'D1' in dists:
            d_d = dists['D1']
            dists['D2'] = d_d + 1


def is_finished(world):
    return world['A1'].startswith('A') and \
           world['A2'].startswith('A') and \
           world['B1'].startswith('B') and \
           world['B2'].startswith('B') and \
           world['C1'].startswith('C') and \
           world['C2'].startswith('C') and \
           world['D1'].startswith('D') and \
           world['D2'].startswith('D')


def get_factor(amphore):
    t = amphore[0]
    if t == 'A':
        return 1
    if t == 'B':
        return 10
    if t == 'C':
        return 100
    if t == 'D':
        return 1000


# moves a POD in the world
def do_move(move, world, pods):
    global distances
    pod = move[0]
    move_to = move[1]
    move_from = pods[pod]
    world[move_from] = ''
    world[move_to] = pod
    pods[pod] = move_to


# how much a move costs
def get_cost(move, pods):
    global distances
    pod = move[0]
    move_to = move[1]
    move_from = pods[pod]

    return distances[move_from][move_to] * get_factor(pod)


# where the slots are located in the floor
def get_pos_for_slot(slot):
    s = slot[0]
    if s == 'A':
        return 3
    if s == 'B':
        return 5
    if s == 'C':
        return 7
    if s == 'D':
        return 9


def is_slot(current_room):
    return ord(current_room[0]) > 64


# check a move whether its legal or not
def is_legal_move(world, current_room, target_room, pod):
    # the target must be empty
    if world[target_room] != '':
        return False

    # if the target is a slot, it must be the correct one
    pod_type = pod[0]
    if is_slot(target_room):
        if target_room[0] != pod_type:
            return False
        # when we move to the top in the slot, the bottom must be occupied by the other pod
        # (so we also move to the deepest position)
        if target_room[1] == '1' and (world[target_room[0] + '2'] == '' or world[target_room[0] + '2'][0] != pod_type):
            return False
        # note: when the bottom is free, the top must be too (because we move as far down as possible)

    # we never move out of a correct slot when we shouldn't
    if is_slot(current_room) and current_room[0] == pod_type:
        # we are in the bottom slot - stay here always
        if current_room[1] == '2':
            return False
        # we are in the top spot, and the bottom is the other pod
        if world[current_room[0] + '2'] == pod_type:
            return False

    # both are in a slot, so check the floor in-between
    if is_slot(current_room) and is_slot(target_room):
        c = get_pos_for_slot(current_room)
        t = get_pos_for_slot(target_room)
        left = min(c, t)
        right = max(c, t)
        for p in range(left, right + 1):
            if world[str(p)] != '':
                return False
        return True

    # from slot to floor, or vice versa
    if is_slot(current_room):
        r = get_pos_for_slot(current_room)
        f = int(target_room)
    else:
        r = get_pos_for_slot(target_room)
        f = int(current_room)
    left = min(r, f)
    right = max(r, f)
    for p in range(left + 1, right):
        if world[str(p)] != '':
            return False

    return True


# gets a list of all possibole moves
def get_moves(world, pods):
    moves = []
    for pod in pods.keys():
        current_room = pods[pod]
        # keep pod when its in the bottom of the correct slot
        bottom_slot = pod[0] + '2'
        top_slot = pod[0] + '1'
        if current_room == bottom_slot:
            continue
        # keep pod when its in the top of the correct slot, and the bottom is the other correct one
        if current_room == top_slot and world[bottom_slot][0] == pod[0]:
            continue
        # keep pod if it cannot move out of its current slot (top is occupied)
        if is_slot(current_room) and current_room[1] == '2' and world[current_room[0] + '1'] != '':
            continue
        # when the pod could move from its current location, check all possible targets for legality
        potential = distances[current_room].keys()
        for target in potential:
            if is_legal_move(world, current_room, target, pod):
                moves.append([pod, target])
                if is_slot(target):
                    break
    return moves


def simulate(world, pods):
    # a move is a pair of amphorae->target_room
    moves = get_moves(world, pods)
    best_cost = 100000

    for move in moves:
        cost = get_cost(move, pods)

        new_world = world.copy()
        new_pods = pods.copy()
        do_move(move, new_world, new_pods)
        if is_finished(new_world):
            if cost < best_cost:
                best_cost = cost
            continue

        sub_cost = simulate(new_world, new_pods)
        if cost + sub_cost < best_cost:
            best_cost = cost + sub_cost
    return best_cost


def memoize(f):
    memo = {}

    def helper(x, y):
        k = str(x)
        if k not in memo:
            memo[k] = f(x, y)
        return memo[k]

    return helper


# we memoize how much finishing from a given state would cost
simulate = memoize(simulate)


def run():
    pods, world = get_real_data()
    fix_distances()
    # pods, world = get_test_data()
    energy = simulate(world, pods)

    print(energy)


if __name__ == '__main__':
    run()
