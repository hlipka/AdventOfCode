import numpy as np


class Board:
    def __init__(self, lines):
        ml = ' '.join(lines).replace('\n', '')
        self.data = np.fromstring(ml, sep=' ')
        self.data = self.data.reshape(5, 5)

    def cross(self, num):
        self.data[self.data == num] = -1

    @staticmethod
    def row_selected(row):
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

    # simulate all boards simultaneously
    for num in nums:
        print("cross " + num)
        for board in boards:
            board.cross(int(num))
            if board.is_winner():
                print("winner")
                print(board.data)
                us = board.get_unselected()
                print(us)
                s = sum(us)
                print(s)
                print(int(num) * s)
                return
    print("no winner found")


if __name__ == '__main__':
    run('../../../data/2021/day04.txt')
