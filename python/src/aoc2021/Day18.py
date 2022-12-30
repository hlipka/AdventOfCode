import math

OPEN = '['
CLOSE = ']'


# parse the number into a list of tokens (brackets or digits)
# this is much simpler to handle than a tree because we can easily go to the lft or right
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
            # when a number explodes, store the left and right parts
            left = number[explode_pos + 1]
            right = number[explode_pos + 2]
            # replace the exploded number by '0'
            number[explode_pos:explode_pos + 4] = [0]
            # and try to add the parts to the left and right
            add_to_left(number, explode_pos - 1, left)
            add_to_right(number, explode_pos + 1, right)
            did_action = True
        else:
            split_pos = find_split(number)
            if split_pos != -1:
                # when we split a number we just insert it into the number
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
    result = None
    for n in numbers:
        if result is None:
            result = n
        else:
            result = add(result, n)
        result = reduce_number(result)
    print(result)
    print(magnitude(result))


def test_reduce():
    print(reduce_number(parse_number("[[[[[9,8],1],2],3],4]")))
    print(reduce_number(parse_number("[7,[6,[5,[4,[3,2]]]]]")))
    print(reduce_number(parse_number("[[6, [5, [4, [3, 2]]]], 1]")))
    print(reduce_number(parse_number("[[3, [2, [1, [7, 3]]]], [6, [5, [4, [3, 2]]]]]")))
    print(reduce_number(parse_number("[[3, [2, [8, 0]]], [9, [5, [4, [3, 2]]]]]")))


if __name__ == '__main__':
    #    test_reduce()
    run('../../../data/2021/day18.txt')
