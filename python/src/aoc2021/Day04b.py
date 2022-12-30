import numpy as np


class Board:
    def __init__(self, lines):
        ml = ' '.join(lines).replace('\n', '')
        self.data = np.fromstring(ml, sep=' ')
        self.data = self.data.reshape(5, 5)

    def cross(self, num):
        self.data[self.data == num] = -1

    def row_selected(self, row):
        return (row == -1).all()

    def is_winner(self):
        for row in self.data:
            if self.row_selected(row):
                return True
        for row in self.data.transpose():
            if self.row_selected(row):
                return True

    def get_unselected(self):
        get = self.data.flat != -1
        return self.data.flat[get]


def run(f_name):
    boards = []
    last_score = 0
    fin = open(f_name)
    line = fin.readline()
    nums = line.replace('\n', '').split(',')
    fin.readline()
    board_lines = []
    for line in fin:
        if line == '\n':
            boards.append(Board(board_lines))
            board_lines = []
        else:
            board_lines.append(line)

    for num in nums:
        print("cross " + num)
        removed = []  # remember which board won in a round
        for board in boards:
            board.cross(int(num))
            if board.is_winner():
                us = board.get_unselected()
                s = sum(us)
                # remember the last score so we can use it later
                last_score = int(num) * s
                removed.append(board)
                print(board.data)
        # and now remove the winning boards from the list
        for b in removed:
            boards.remove(b)
        if 0 == len(boards):
            print("done")
            break
    print(last_score)


if __name__ == '__main__':
    run('../data/day04.txt')
