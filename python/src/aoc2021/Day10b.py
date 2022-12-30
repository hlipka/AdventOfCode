import math


def parse_char(c, stack):
    if c == '(':
        stack.append(c)
    if c == '[':
        stack.append(c)
    if c == '{':
        stack.append(c)
    if c == '<':
        stack.append(c)
    if c == ')':
        c1 = stack.pop()
        return c1 == '('
    if c == ']':
        c1 = stack.pop()
        return c1 == '['
    if c == '}':
        c1 = stack.pop()
        return c1 == '{'
    if c == '>':
        c1 = stack.pop()
        return c1 == '<'
    return True


def parse_line(line):
    stack = []
    for c in line:
        if not parse_char(c, stack):
            return 0  # discard corrupted lines (with non-matching chars)
    # the needed characters can be found from the reversed stack
    stack.reverse()
    score = 0
    for c in stack:
        if c == '(':
            score = score * 5 + 1
        if c == '[':
            score = score * 5 + 2
        if c == '{':
            score = score * 5 + 3
        if c == '<':
            score = score * 5 + 4
    return score


def run(f_name):
    scores = []
    fin = open(f_name)
    for line in fin:
        if line.strip() != "":
            score = parse_line(line.strip())
            if 0 != score:
                scores.append(score)
    scores.sort()
    print(scores)
    print(scores[math.floor(len(scores) / 2)])


if __name__ == '__main__':
    run('../data/day10.txt')
