class Player:
    def __init__(self, pos):
        self.pos = pos - 1
        self.score = 0

    def move(self, moves):
        self.pos = (self.pos + moves) % 10
        self.score += self.pos + 1

    def get_score(self):
        return self.score


class Game:
    def __init__(self, p1, p2):
        self.players = [p1, p2]
        self.player = 1

    def next_player(self):
        self.player = 1 - self.player
        return self.players[self.player]


def roll_die(sides):
    while True:
        for i in range(1, sides + 1):
            yield i


class Die:
    def __init__(self, sides):
        self.rolls = 0
        self.die = roll_die(sides)

    def roll(self):
        self.rolls += 1
        return next(self.die)

    def roll_count(self):
        return self.rolls


def run(p1, p2):
    player1 = Player(p1)
    player2 = Player(p2)
    game = Game(player1, player2)
    die = Die(100)

    while True:
        player = game.next_player()
        move = die.roll() + die.roll() + die.roll()
        player.move(move)
        if player.get_score() >= 1000:
            other_player = game.next_player()
            print(other_player.get_score() * die.roll_count())
            return


if __name__ == '__main__':
    run(4, 8)
    run(9, 6)
