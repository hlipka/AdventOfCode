import numpy as np


class Cell:
    def __init__(self, energy):
        self.energy = energy
        self.nb = []
        self.flashes = 0
        self.mark = False
        self.x = -1
        self.y = -1

    def __repr__(self):
        m = ' '
        if self.mark:
            m = '*'
        return str('%d%s' % (self.energy, m))

    def set_pos(self, x, y):
        self.x = x
        self.y = y

    def add_neighbour(self, cell):
        self.nb.append(cell)

    def round(self):
        self.energy = self.energy + 1

    def do_flash(self):
        # when the cell has high enough energy and has not flashed in this round
        # flash itself, distribute energy to neighbours
        if self.energy > 9 and self.mark is False:
            self.mark = True
            self.flashes = self.flashes + 1
            for c in self.nb:
                c.increase()

    def increase(self):
        self.energy = self.energy + 1
        # when the flash from a neighbour increases energy enough, fash this cell as well
        if self.energy > 9 and self.mark is False:
            self.do_flash()

    def did_flash(self):
        return self.mark

    def end_round(self):
        self.mark = False
        if self.energy > 9:
            self.energy = 0


def parse_line(line, cells):
    for c in line:
        cells.append(Cell(int(c) - int('0')))


def fill_neighbour(cell, cells, x, y):
    if x < 0 or x > 9:
        return
    if y < 0 or y > 9:
        return
    cell.add_neighbour(cells[x, y])


def fill_neighbour_cells(cell, cells, x, y):
    fill_neighbour(cell, cells, x - 1, y - 1)
    fill_neighbour(cell, cells, x, y - 1)
    fill_neighbour(cell, cells, x + 1, y - 1)
    fill_neighbour(cell, cells, x - 1, y)
    fill_neighbour(cell, cells, x + 1, y)
    fill_neighbour(cell, cells, x - 1, y + 1)
    fill_neighbour(cell, cells, x, y + 1)
    fill_neighbour(cell, cells, x + 1, y + 1)


def fill_neighbours(cells):
    for x in range(0, 10):
        for y in range(0, 10):
            cell = cells[x, y]
            fill_neighbour_cells(cell, cells, x, y)
            cell.set_pos(x, y)


def run(f_name):
    cell_data = []
    fin = open(f_name)
    # parse the map into a list of cells
    for line in fin:
        parse_line(line.strip(), cell_data)

    # make a map from the list
    cells = np.array(cell_data, dtype=object).reshape((10, 10))
    # each cell needs to know its neighbours
    fill_neighbours(cells)
    # it is easier to handle if we have a flat array instead of a map
    all_cells = np.reshape(cells, 100)
    flashes = 0
    for round_number in range(0, 100):
        # increase energy in the cells
        for cell in all_cells:
            cell.round()
        # flash all cells - this propagates energy and flashes other cells as well
        for cell in all_cells:
            cell.do_flash()
        # count cells which did flash
        for cell in all_cells:
            if cell.did_flash():
                flashes = flashes + 1
        # reset energy values and flash markers
        for cell in all_cells:
            cell.end_round()
        print(round_number, flashes)


if __name__ == '__main__':
    run('../data/day11.txt')
