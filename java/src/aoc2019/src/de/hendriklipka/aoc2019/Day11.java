package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.InfiniteCharMatrix;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

public class Day11 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day11().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> code = data.getFirstLineWords(",");
        IntCode intCode = IntCode.fromStringList(code);
        IntCode.Pipe pipe=new IntCode.Pipe();
        intCode.setDoInput(pipe);
        Painter painter=new Painter(pipe, 'b');
        intCode.setDoOutput(painter);
        intCode.execute();
        return painter.paintedFields();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> code = data.getFirstLineWords(",");
        IntCode intCode = IntCode.fromStringList(code);
        IntCode.Pipe pipe = new IntCode.Pipe();
        intCode.setDoInput(pipe);
        Painter painter = new Painter(pipe, 'w');
        intCode.setDoOutput(painter);
        intCode.execute();
        painter.draw();
        return -1;
    }

    private static class Painter implements Consumer<BigInteger>
    {
        InfiniteCharMatrix hull;
        Position pos=new Position(0, 0);
        Direction direction=Direction.UP;
        private final IntCode.Pipe _pipe;
        boolean doPaint=true;

        public Painter(final IntCode.Pipe pipe, final char startPanel)
        {
            _pipe = pipe;
            hull = new InfiniteCharMatrix(startPanel);
            _pipe.accept(hull.at(pos) == 'b' ? BigInteger.ZERO : BigInteger.ONE);
        }

        @Override
        public void accept(final BigInteger value)
        {
            if (doPaint)
            {
                hull.set(pos, value.equals(BigInteger.ZERO)?'b':'w');
            }
            else
            {
                direction= value.equals(BigInteger.ZERO)?direction.left():direction.right();
                pos=pos.updated(direction);
                _pipe.accept(hull.at(pos)=='b'?BigInteger.ZERO:BigInteger.ONE);
            }
            doPaint=!doPaint;
        }

        public int paintedFields()
        {
            return hull.allKnownTiles().size();
        }

        public void draw()
        {
            final var matrix = hull.getSubMatrix(new Position(hull.getMinRow() - 1, hull.lowestColumn() - 1), hull.rows() + 2,
                    hull.columns() + 2);
            matrix.replace('b', ' ');
            matrix.replace('w', '#');
            matrix.print();
        }
    }
}
