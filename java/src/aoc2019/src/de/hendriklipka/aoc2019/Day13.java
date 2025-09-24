package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.InfiniteCharMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Day13 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day13().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> code = data.getFirstLineWords(",");
        IntCode intCode = IntCode.fromStringList(code);
        Painter painter = new Painter();
        intCode.setDoOutput(painter);
        intCode.execute();
        final var screen = painter.matrix;
        final var matrix = screen.getSubMatrix(new Position(screen.getMinRow() - 1, screen.lowestColumn() - 1), screen.rows() + 2,
                screen.columns() + 2);
        matrix.print();

        return screen.allKnownTiles('b').size();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> code = new ArrayList<>(data.getFirstLineWords(","));
        code.set(0,"2"); // insert coins
        IntCode intCode = IntCode.fromStringList(code);
        Player player = new Player();
        intCode.setDoOutput(player);
        intCode.setDoInput(player);
        // exists when the player lost, or all blocks are gone
        intCode.execute();

        System.out.println("blocks left: "+ player.matrix.allKnownTiles('b').size());
        return player.getScore();
    }

    private static class Painter implements Consumer<Integer>
    {
        InfiniteCharMatrix matrix=new InfiniteCharMatrix(' ');
        int state=0;
        int x=0;
        int y=0;

        @Override
        public void accept(final Integer value)
        {
            switch (state)
            {
                case 0: x=value;
                        break;
                case 1: y=value;
                        break;
                case 2:
                    matrix.set(new Position(y,x), getTile(value) );
            }
            state=(state+1)%3;
        }
    }

    private static class Player implements Consumer<Integer>, Supplier<Integer>
    {
        InfiniteCharMatrix matrix=new InfiniteCharMatrix(' ');
        int state=0;
        int x=0;
        int y=0;

        private int score;

        int paddleX=0;
        private int ballX;

        @Override
        public void accept(final Integer value)
        {
            switch (state)
            {
                case 0: x=value;
                        break;
                case 1: y=value;
                        break;
                case 2:
                    // track the score
                    if (x==-1 && y==0)
                    {
                        score=value;
                    }
                    else
                    {

                        final var tile = getTile(value);
                        // not exactly needed, but it allows us to se whether we finished properly
                        matrix.set(new Position(y, x), tile);
                        // track position of paddle and ball
                        if ('o'==tile)
                        {
                            ballX=x;
                        }
                        if ('-'==tile)
                        {
                            paddleX=x;
                        }
                    }
            }
            state=(state+1)%3;
        }

        public int getScore()
        {
            return score;
        }

        // when we get asked for input, move the paddle towards the ball
        @Override
        public Integer get()
        {
            if (paddleX==ballX)
                return 0;
            if (paddleX < ballX)
                return 1;
            return -1;
        }
    }

    private static char getTile(final int value)
    {
        return switch (value)
        {
            case 0 -> ' ';
            case 1 -> 'w';
            case 2 -> 'b';
            case 3 -> '-';
            case 4 -> 'o';
            default -> throw new IllegalArgumentException("invalid tile num " + value);
        };
    }
}
