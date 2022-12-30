from math import trunc

count = 0
results = []


# this is the generated code from Day24_gen.py
def step1(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 1)
    x = x + 12
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 4
    y = y * x
    z = z + y
    return z


def step2(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 1)
    x = x + 11
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 11
    y = y * x
    z = z + y
    return z


def step3(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 1)
    x = x + 13
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 5
    y = y * x
    z = z + y
    return z


def step4(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 1)
    x = x + 11
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 11
    y = y * x
    z = z + y
    return z


def step5(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 1)
    x = x + 14
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 14
    y = y * x
    z = z + y
    return z


def step6(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 26)
    x = x + -10
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 7
    y = y * x
    z = z + y
    return z


def step7(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 1)
    x = x + 11
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 11
    y = y * x
    z = z + y
    return z


def step8(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 26)
    x = x + -9
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 4
    y = y * x
    z = z + y
    return z


def step9(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 26)
    x = x + -3
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 6
    y = y * x
    z = z + y
    return z


def step10(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 1)
    x = x + 13
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 5
    y = y * x
    z = z + y
    return z


def step11(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 26)
    x = x + -5
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 9
    y = y * x
    z = z + y
    return z


def step12(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 26)
    x = x + -10
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 12
    y = y * x
    z = z + y
    return z


def step13(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 26)
    x = x + -4
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 14
    y = y * x
    z = z + y
    return z


def step14(w, z):
    x = 0
    x = x + z
    x = x % 26
    z = trunc(z / 26)
    x = x + -5
    x = 1 if x == w else 0
    x = 1 if x == 0 else 0
    y = 0
    y = y + 25
    y = y * x
    y = y + 1
    z = z * y
    y = 0
    y = y + w
    y = y + 14
    y = y * x
    z = z + y
    return z


# end generated code

# we work backwards through the MONAD code
# each step only uses Z from the previous step, and takes W as its input (from 1..9)
# we know step 14 must end up in 'z==0', so we check all possible inputs to get a list of possible Z input values
# then we do the same with step13, getting a list of possible Z inputs which generate a Z from the previous set
# this way we work upwards until we run 'step1'. There we must have a possible z input of 0, because this is the
# startup condition for the ALU
# when we now have the list of usable Z values after each step, we can start from step 1 (which gets the highest digit
# as input) and determine the largest W value which results in a usable Z value. We then take this Z value for step2
# and again look for the biggest W input. We work upwards until step 14, until we have the full serial
# Similar for the smallest serial.
def run():
    next_z = {}
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, 1000):
            if 0 == step14(w, z):
                potential_next.add(z)
    print(potential_next)

    next_z[14] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step13(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[13] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step12(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[12] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step11(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[11] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step10(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[10] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step9(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[9] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step8(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[8] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step7(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[7] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step6(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[6] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step5(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[5] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step4(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[4] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step3(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[3] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step2(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)

    next_z[2] = potential_next
    last_z = potential_next
    potential_next = set()
    for w in range(1, 10):
        for z in range(0, max(last_z) * 50):
            if step1(w, z) in last_z:
                potential_next.add(z)
    print(potential_next)
    if 0 not in potential_next:
        print("no valid inputs found")
        return

    calculate_max(next_z)
    calculate_min(next_z)


def calculate_max(next_z):
    result = []
    this_z = 0
    z = 0
    potential_next = next_z[2]
    for w in range(9, 0, -1):
        z = step1(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[3]
    this_z = z
    for w in range(9, 0, -1):
        z = step2(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[4]
    this_z = z
    for w in range(9, 0, -1):
        z = step3(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[5]
    this_z = z
    for w in range(9, 0, -1):
        z = step4(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[6]
    this_z = z
    for w in range(9, 0, -1):
        z = step5(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[7]
    this_z = z
    for w in range(9, 0, -1):
        z = step6(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[8]
    this_z = z
    for w in range(9, 0, -1):
        z = step7(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[9]
    this_z = z
    for w in range(9, 0, -1):
        z = step8(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[10]
    this_z = z
    for w in range(9, 0, -1):
        z = step9(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[11]
    this_z = z
    for w in range(9, 0, -1):
        z = step10(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[12]
    this_z = z
    for w in range(9, 0, -1):
        z = step11(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[13]
    this_z = z
    for w in range(9, 0, -1):
        z = step12(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[14]
    this_z = z
    for w in range(9, 0, -1):
        z = step13(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    this_z = z
    for w in range(9, 0, -1):
        z = step14(w, this_z)
        if z == 0:
            result.append(w)
            break
    serial = ''.join(list(map(lambda d: str(d), result)))
    print(serial)


def calculate_min(next_z):
    result = []
    this_z = 0
    z = 0
    potential_next = next_z[2]
    for w in range(1, 10):
        z = step1(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[3]
    this_z = z
    for w in range(1, 10):
        z = step2(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[4]
    this_z = z
    for w in range(1, 10):
        z = step3(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[5]
    this_z = z
    for w in range(1, 10):
        z = step4(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[6]
    this_z = z
    for w in range(1, 10):
        z = step5(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[7]
    this_z = z
    for w in range(1, 10):
        z = step6(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[8]
    this_z = z
    for w in range(1, 10):
        z = step7(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[9]
    this_z = z
    for w in range(1, 10):
        z = step8(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[10]
    this_z = z
    for w in range(1, 10):
        z = step9(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[11]
    this_z = z
    for w in range(1, 10):
        z = step10(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[12]
    this_z = z
    for w in range(1, 10):
        z = step11(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[13]
    this_z = z
    for w in range(1, 10):
        z = step12(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    potential_next = next_z[14]
    this_z = z
    for w in range(1, 10):
        z = step13(w, this_z)
        if z in potential_next:
            result.append(w)
            break
    this_z = z
    for w in range(1, 10):
        z = step14(w, this_z)
        if z == 0:
            result.append(w)
            break
    serial = ''.join(list(map(lambda d: str(d), result)))
    print(serial)


if __name__ == '__main__':
    run()
