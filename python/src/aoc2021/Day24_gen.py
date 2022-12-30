input_count = 0


# parse each input line, write a corresponding python code line
def parse(line):
    global input_count
    parts = line.split(' ')
    if parts[0] == 'inp':
        input_count += 1
        # on input, we finish the previous function, and start a new one
        print("    return z\n\ndef step" + str(input_count) + "(w, z):")
    if parts[0] == 'add':
        print("   ", parts[1], "=", parts[1], "+", parts[2])
    if parts[0] == 'mul':
        if parts[2] == '0':
            print("   ", parts[1], "= 0")
        else:
            print("   ", parts[1], "=", parts[1], "*", parts[2])
    if parts[0] == 'div':
        print("   ", parts[1], "= trunc(", parts[1], "/", parts[2], ")")
    if parts[0] == 'mod':
        print("   ", parts[1], "=", parts[1], "%", parts[2])
    if parts[0] == 'eql':
        print("   ", parts[1], "= 1 if", parts[1], "==", parts[2], "else 0")


def run(f_name):
    fin = open(f_name)

    for line in fin:
        line = line.strip()
        if line == '':
            continue
        parse(line)


if __name__ == '__main__':
    run('../../../data/2021/day24.txt')
