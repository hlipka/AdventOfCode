import math

OPEN = '['
CLOSE = ']'


def parse_number(line):
    number = []
    for c in line:
        if c == OPEN:
            number.append(OPEN)
        elif c == CLOSE:
            number.append(CLOSE)
        elif c == ',':
            pass
        elif c == ' ':
            pass
        else:
            number.append(int(c))
    return number


def add(num1, num2):
    result = [OPEN]
    result.extend(num1)
    result.extend(num2)
    result.append(CLOSE)
    return result


def find_explode(number):
    open_count = 0
    pos = 0
    for el in number:
        if el == OPEN:
            open_count += 1
        if el == CLOSE:
            open_count -= 1
        if open_count == 5:
            return pos
        pos += 1
    return -1


def add_to_left(number, explode_pos, value):
    for pos in range(explode_pos, 0, -1):
        el = number[pos]
        if el != OPEN and el != CLOSE:
            number[pos] = el + value
            return


def add_to_right(number, explode_pos, value):
    for pos in range(explode_pos, len(number)):
        el = number[pos]
        if el != OPEN and el != CLOSE:
            number[pos] = el + value
            return


def find_split(number):
    for pos in range(0, len(number)):
        el = number[pos]
        if el != OPEN and el != CLOSE and el > 9:
            return pos
    return -1


def reduce_number(number):
    did_action = True
    while did_action:
        explode_pos = find_explode(number)
        did_action = False
        if explode_pos != -1:
            #            print("explode")
            left = number[explode_pos + 1]
            right = number[explode_pos + 2]
            number[explode_pos:explode_pos + 4] = [0]
            add_to_left(number, explode_pos - 1, left)
            add_to_right(number, explode_pos + 1, right)
            did_action = True
        else:
            split_pos = find_split(number)
            if split_pos != -1:
                #                print("split")
                value = number[split_pos]
                half = value / 2
                number[split_pos:split_pos + 1] = [OPEN, math.floor(half), math.ceil(half), CLOSE]
                did_action = True
    return number


def mag1(number):
    for pos in range(0, len(number) - 3):
        if number[pos] == OPEN and number[pos + 3] == CLOSE \
                and isinstance(number[pos + 1], int) and isinstance(number[pos + 2], int):
            number[pos:pos + 4] = [3 * number[pos + 1] + 2 * number[pos + 2]]
            return number
    return None


def magnitude(number):
    while len(number) > 3:
        number = mag1(number)
    return number[0]


def run(f_name):
    numbers = []
    fin = open(f_name)
    for line in fin:
        if line.strip() != "":
            number = parse_number(line.strip())
            numbers.append(number)
    result = 0
    # just run through all combinations and check for the highest magnitude
    for a in range(0, len(numbers)):
        for b in range(0, len(numbers)):
            if a != b:
                first = numbers[a]
                second = numbers[b]
                num = add(first, second)
                mag = magnitude(reduce_number(num).copy())
                if mag > result:
                    result = mag
    print(result)


def test_reduce():
    print(reduce_number(parse_number("[[[[[9,8],1],2],3],4]")))
    print(reduce_number(parse_number("[7,[6,[5,[4,[3,2]]]]]")))
    print(reduce_number(parse_number("[[6, [5, [4, [3, 2]]]], 1]")))
    print(reduce_number(parse_number("[[3, [2, [1, [7, 3]]]], [6, [5, [4, [3, 2]]]]]")))
    print(reduce_number(parse_number("[[3, [2, [8, 0]]], [9, [5, [4, [3, 2]]]]]")))


if __name__ == '__main__':
    #    test_reduce()
    run('../data/day18.txt')
