win_p1 = 0
win_p2 = 0


def do_player1(die, p1, score1, p2, score2, win_m):
    p1 = (p1 + die) % 10
    score1 += p1 + 1
    global win_p1
    if score1 >= 21:
        win_p1 += win_m
        return

    do_player2(3, p1, score1, p2, score2, win_m)
    do_player2(4, p1, score1, p2, score2, win_m * 3)
    do_player2(5, p1, score1, p2, score2, win_m * 6)
    do_player2(6, p1, score1, p2, score2, win_m * 7)
    do_player2(7, p1, score1, p2, score2, win_m * 6)
    do_player2(8, p1, score1, p2, score2, win_m * 3)
    do_player2(9, p1, score1, p2, score2, win_m)


def do_player2(die, p1, score1, p2, score2, win_m):
    p2 = (p2 + die) % 10
    score2 += p2 + 1
    global win_p2
    if score2 >= 21:
        win_p2 += win_m
        return

    do_player1(3, p1, score1, p2, score2, win_m)
    do_player1(4, p1, score1, p2, score2, win_m * 3)
    do_player1(5, p1, score1, p2, score2, win_m * 6)
    do_player1(6, p1, score1, p2, score2, win_m * 7)
    do_player1(7, p1, score1, p2, score2, win_m * 6)
    do_player1(8, p1, score1, p2, score2, win_m * 3)
    do_player1(9, p1, score1, p2, score2, win_m)


def run(p1, p2):
    global win_p1
    global win_p2
    win_p1 = 0
    win_p2 = 0

    # we know often each sum of 3 dice rolls occur
    # so let's simulate each sum in one call, and keep track of how often it happens
    # these occurrences multiply with each other with each round
    do_player1(3, p1 - 1, 0, p2 - 1, 0, 1)
    do_player1(4, p1 - 1, 0, p2 - 1, 0, 3)
    do_player1(5, p1 - 1, 0, p2 - 1, 0, 6)
    do_player1(6, p1 - 1, 0, p2 - 1, 0, 7)
    do_player1(7, p1 - 1, 0, p2 - 1, 0, 6)
    do_player1(8, p1 - 1, 0, p2 - 1, 0, 3)
    do_player1(9, p1 - 1, 0, p2 - 1, 0, 1)

    print(win_p1, win_p2)


if __name__ == '__main__':
    run(4, 8)
    run(9, 6)
