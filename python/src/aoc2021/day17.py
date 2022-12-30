# one of the downward steps ends up exactly at y=0 (because it is a mirror of the upwards steps)
# the farthest next step is then ending up at y_min
# so the initial speed is y_min-1
# and the height is the sum of all numbers up to that value
def calculate(min_y):
    return min_y * (min_y - 1) / 2


def run(min_x, max_x, min_y, max_y):
    print(calculate(-min_y))


if __name__ == '__main__':
    run(128, 160, -142, -88)
