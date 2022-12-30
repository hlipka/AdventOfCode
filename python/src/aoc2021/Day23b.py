# this is the same as part 1, but we have a bigger starting state (as the slots are now 4 deep)
# some of the checks are now generic to handle all 4 slot positions

# map pods to their rooms
def map_pods_to_rooms(world):
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
        'A1': 'D1', 'A2': 'D2', 'A3': 'D3', 'A4': 'D4',
        'B1': 'B1', 'B2': 'C1', 'B3': 'B2', 'B4': 'A1',
        'C1': 'C2', 'C2': 'B3', 'C3': 'A2', 'C4': 'B4',
        'D1': 'C3', 'D2': 'A3', 'D3': 'C4', 'D4': 'A4',
    }
    pods = map_pods_to_rooms(world)
    return pods, world


def get_test_data():
    world = {
        # floors are empty
        '1': '', '2': '', '3': '', '4': '', '5': '', '6': '', '7': '', '8': '', '9': '', '10': '', '11': '',
        # rooms are occupied
        'A1': 'B1', 'A2': 'D1', 'A3': 'D2', 'A4': 'A1',
        'B1': 'C1', 'B2': 'C2', 'B3': 'B2', 'B4': 'D3',
        'C1': 'B3', 'C2': 'B4', 'C3': 'A2', 'C4': 'C3',
        'D1': 'D4', 'D2': 'A3', 'D3': 'C4', 'D4': 'A4',
    }
    pods = map_pods_to_rooms(world)
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
    'A3': {'B1': 6,
           'C1': 8,
           'D1': 10,
           '1': 5, '2': 4, '4': 4, '6': 6, '8': 9, '10': 10, '11': 11},
    'A4': {'B1': 7,
           'C1': 9,
           'D1': 11,
           '1': 6, '2': 5, '4': 5, '6': 7, '8': 10, '10': 11, '11': 12},
    'B1': {'A1': 4,
           'C1': 4,
           'D1': 6,
           '1': 5, '2': 4, '4': 2, '6': 2, '8': 4, '10': 6, '11': 7},
    'B2': {'A1': 5,
           'C1': 5,
           'D1': 7,
           '1': 6, '2': 5, '4': 3, '6': 3, '8': 5, '10': 7, '11': 8},
    'B3': {'A1': 6,
           'C1': 6,
           'D1': 7,
           '1': 7, '2': 6, '4': 4, '6': 4, '8': 6, '10': 8, '11': 9},
    'B4': {'A1': 7,
           'C1': 7,
           'D1': 9,
           '1': 8, '2': 7, '4': 5, '6': 5, '8': 7, '10': 9, '11': 10},
    'C1': {'A1': 6,
           'B1': 4,
           'D1': 4,
           '1': 7, '2': 6, '4': 4, '6': 2, '8': 2, '10': 4, '11': 5},
    'C2': {'A1': 7,
           'B1': 5,
           'D1': 5,
           '1': 8, '2': 7, '4': 5, '6': 3, '8': 3, '10': 5, '11': 6},
    'C3': {'A1': 8,
           'B1': 6,
           'D1': 6,
           '1': 9, '2': 8, '4': 6, '6': 4, '8': 4, '10': 6, '11': 7},
    'C4': {'A1': 9,
           'B1': 7,
           'D1': 7,
           '1': 10, '2': 9, '4': 7, '6': 5, '8': 5, '10': 7, '11': 8},
    'D1': {'A1': 8,
           'B1': 6,
           'C1': 4,
           '1': 9, '2': 8, '4': 6, '6': 4, '8': 2, '10': 2, '11': 3},
    'D2': {'A1': 9,
           'B1': 7,
           'C1': 5,
           '1': 10, '2': 9, '4': 7, '6': 5, '8': 3, '10': 3, '11': 4},
    'D3': {'A1': 10,
           'B1': 8,
           'C1': 6,
           '1': 11, '2': 10, '4': 8, '6': 6, '8': 4, '10': 4, '11': 5},
    'D4': {'A1': 11,
           'B1': 9,
           'C1': 7,
           '1': 12, '2': 11, '4': 9, '6': 7, '8': 5, '10': 5, '11': 6},
}


# add the remaining distances for the slots
def fix_distances():
    global distances
    for room in distances:
        dists = distances[room]

        if 'A1' in dists:
            d_a = dists['A1']
            dists['A2'] = d_a + 1
            dists['A3'] = d_a + 2
            dists['A4'] = d_a + 3

        if 'B1' in dists:
            d_b = dists['B1']
            dists['B2'] = d_b + 1
            dists['B3'] = d_b + 2
            dists['B4'] = d_b + 3

        if 'C1' in dists:
            d_c = dists['C1']
            dists['C2'] = d_c + 1
            dists['C3'] = d_c + 2
            dists['C4'] = d_c + 3

        if 'D1' in dists:
            d_d = dists['D1']
            dists['D2'] = d_d + 1
            dists['D3'] = d_d + 2
            dists['D4'] = d_d + 3


def is_finished(world):
    return world['A1'].startswith('A') and \
           world['A2'].startswith('A') and \
           world['A3'].startswith('A') and \
           world['A4'].startswith('A') and \
           world['B1'].startswith('B') and \
           world['B2'].startswith('B') and \
           world['B3'].startswith('B') and \
           world['B4'].startswith('B') and \
           world['C1'].startswith('C') and \
           world['C2'].startswith('C') and \
           world['C3'].startswith('C') and \
           world['C4'].startswith('C') and \
           world['D1'].startswith('D') and \
           world['D2'].startswith('D') and \
           world['D3'].startswith('D') and \
           world['D4'].startswith('D')


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


def do_move(move, world, pods):
    global distances
    pod = move[0]
    move_to = move[1]
    move_from = pods[pod]

    world[move_from] = ''
    world[move_to] = pod
    pods[pod] = move_to


def get_cost(move, pods):
    global distances
    pod = move[0]
    move_to = move[1]
    move_from = pods[pod]

    return distances[move_from][move_to] * get_factor(pod)


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


def is_legal_move(world, current_room, target_room, pod):
    # the target must be empty
    if world[target_room] != '':
        return False

    # if the target is a slot, it must be the correct one
    pod_type = pod[0]
    if is_slot(target_room):
        if target_room[0] != pod_type:
            return False
        # when we move a slot, we go either to the deepest position, or anything else must be of the same type
        # (so we also move to the deepest position)
        if deeper_slots_not_occupied_by_same(pod_type, target_room, world):
            return False

    # both are in a slot, so check the floor in-between
    if is_slot(current_room) and is_slot(target_room):
        # we have already checked the current slot for blocker, so check the target
        if not can_move_to_target_slot(target_room, world):
            return False
        # now check the floor
        c = get_pos_for_slot(current_room)
        t = get_pos_for_slot(target_room)
        left = min(c, t)
        right = max(c, t)
        for p in range(left, right + 1):
            if world[str(p)] != '':
                return False
        return True

    # from slot to floor, or vice versa
    # we already checked target or start slots for blockages, so we check only the floor
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


def can_move_to_target_slot(target_room, world):
    target_slot_pos = int(target_room[1])
    for pos in range(1, target_slot_pos + 1):
        if world[target_room[0] + str(pos)] != '':
            return False
    return True


# True when anything below the target_room is a pod of a different type ('pod_type')
def deeper_slots_not_occupied_by_same(pod_type, target_room, world):
    target_slot_pos = int(target_room[1])
    for pos in range(target_slot_pos + 1, 5):  # no loop when we are at pos 4, and then we are fine
        target = world[target_room[0] + str(pos)]
        if target == '' or target[0] != pod_type:
            return True
    return False


#    return target_room[1] == '1' and (world[target_room[0] + '2'] == '' or world[target_room[0] + '2'][0] != pod_type)


def pod_is_in_correct_slot_position(world, current_room, pod):
    # check for slot itself
    if current_room[0] != pod[0]:
        return False
    # check position - either pod is at the bottom, or anything underneath is of the same type
    current_slot_pos = int(current_room[1])
    for pos in range(current_slot_pos + 1, 5):
        room = world[pod[0] + str(pos)]
        if room == '':
            return False
        if room[0] != pod[0]:
            return False
    return True


def pod_can_move_from_slot(current_room, world):
    # if any slot position above the current one is occupied, we cannot move
    current_slot_pos = int(current_room[1])
    for pos in range(1, current_slot_pos):
        if world[current_room[0] + str(pos)] != '':
            return False
    return True


def get_moves(world, pods):
    moves = []
    for pod in pods.keys():
        current_room = pods[pod]
        # keep pod when its in its target slot, and the slot below is also correct
        if pod_is_in_correct_slot_position(world, current_room, pod):
            continue
        # keep pod if it cannot move out of its current slot (top is occupied)
        if is_slot(current_room) and not pod_can_move_from_slot(current_room, world):
            continue

        potential = distances[current_room].keys()
        for target in potential:
            if is_legal_move(world, current_room, target, pod):
                moves.append([pod, target])
                # when moving to the final slot is possible, there is no better move, so we skip anything else
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


simulate = memoize(simulate)


def run():
    print("expected:", 44169)

    fix_distances()
    pods, world = get_real_data()
    # pods, world = get_test_data()
    energy = simulate(world, pods)

    print(energy)


if __name__ == '__main__':
    run()
