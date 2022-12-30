import numpy as np


def parse_coord(line):
    coords = line.split(',')
    return coords[0], coords[1], coords[2]


def parse_rules(line):
    return list(map(lambda c: 1 if c == '#' else 0, list(line)))


def parse_image_line(line):
    return list(map(lambda c: 1 if c == '#' else 0, list(line)))


def bits_to_number(bits):
    num = 0
    pos = 1
    for b in reversed(bits):
        num += b * pos
        pos *= 2
    return num


def get_pixel(window, rules):
    value = bits_to_number(list(window.flat))
    return rules[value]


def enhance(image, rules, round):
    # pad image according to current round (round 0: 0, round 1: 1, round 2: 0...) adding 2 number in each direction
    # (so determining the new pixels works correctly)
    pad = round % 2
    image = np.pad(image, 2, constant_values=pad)

    # fill target image according to current round (round 0: 1, round 1: 0, round 2: 1...), with the same dimension
    # as the padded source, so the target pixels are already correct
    if 1 == pad:
        target = np.zeros(image.shape, dtype=int)
    else:
        target = np.ones(image.shape, dtype=int)
    # run through all pixels of image, +1 border pixel since the image grows
    for x in range(1, image.shape[0] - 1):
        for y in range(1, image.shape[1] - 1):
            target[x, y] = get_pixel(image[x - 1:x + 2, y - 1:y + 2], rules)
    return target


def enhance_image(img, rules, rounds):
    target = img
    for r in range(0, rounds):
        target = enhance(target, rules, r)
    return target


def run(f_name):
    rules = None
    image = []
    fin = open(f_name)
    for line in fin:
        line = line.strip()
        if line != '':
            if rules is None:
                rules = parse_rules(line)
            else:
                image.append(parse_image_line(line))
    img = np.array(image, dtype=int)

    img = enhance_image(img, rules, 50)  # 2 rounds for part 1
    print(sum(img.flat))


if __name__ == '__main__':
    run('../data/day20.txt')
    # day20test.txt has the same problem as the real data set (flipping all pixels every time),
    # its known solution for 2 passes is 5326
