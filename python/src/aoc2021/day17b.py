from math import sqrt


def simulate(v_x_start, v_y_start, min_x, max_x, min_y, max_y):
    v_x = v_x_start
    v_y = v_y_start
    x = 0
    y = 0
    while True:
        x += v_x
        y += v_y
        # we stop when we overshoot
        if x > max_x or y < min_y:
            return False
        if x >= min_x and y <= max_y:
            return True
        if v_x > 0:
            v_x -= 1
        v_y -= 1


def run(min_x, max_x, min_y, max_y):
    # calculate min and max speed which could hit the target area
    v_x_min = int(sqrt(2 * min_x))
    v_x_max = max_x  # one step to the right side of the target
    v_y_min = min_y  # one step to the bottom of the target
    v_y_max = -min_y - 1  # see first part
    count = 0
    # brute force all combinations
    for v_y in range(v_y_min, v_y_max + 1):
        for v_x in range(v_x_min, v_x_max + 1):
            if simulate(v_x, v_y, min_x, max_x, min_y, max_y):
                count += 1
    print(count)


if __name__ == '__main__':
    run(20, 30, -10, -5)
    run(128, 160, -142, -88)
