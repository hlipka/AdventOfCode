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


# parse each line, push opening characters on the stack and pop them off when the closing one is found
def parse_line(line):
    stack = []
    for c in line:
        # when the wrong char was found on the stack, calculate the score
        if not parse_char(c, stack):
            if c == ')':
                return 3
            if c == ']':
                return 57
            if c == '}':
                return 1197
            if c == '>':
                return 25137
    return 0


def run(f_name):
    score = 0
    fin = open(f_name)
    for line in fin:
        if line.strip() != "":
            score = score + parse_line(line.strip())

    print(score)


if __name__ == '__main__':
    run('../data/day10.txt')
