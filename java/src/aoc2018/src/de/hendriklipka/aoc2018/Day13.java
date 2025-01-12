package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.hendriklipka.aoc.Direction.*;

public class Day13 extends AocPuzzle
{
    private static final Map<Character, Direction> directions=Map.of('^', UP, 'v', DOWN, '<', LEFT, '>', RIGHT);

    public static void main(String[] args)
    {
        new Day13().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix tracks=data.getLinesAsCharMatrix(' ');
        final Map<Position, Pair<Direction, Character>> carts = new HashMap<>();
        // collect the carts, and write the proper track to the map
        getCarts(tracks, carts);
        Position crash=null;
        while (null==crash)
        {
            crash = simulateRound(carts, tracks);
        }
        return crash.col+","+crash.row;
    }

    private static void getCarts(final CharMatrix tracks, final Map<Position, Pair<Direction, Character>> carts)
    {
        for (Position p: tracks.allPositions())
        {
            char c = tracks.at(p);
            Direction d = directions.get(c);
            if (null != d)
            {
                carts.put(p, Pair.of(d, 'l'));
                switch (d)
                {
                    case UP, DOWN -> tracks.set(p, '|');
                    case LEFT, RIGHT -> tracks.set(p, '-');
                }
            }
        }
    }

    private static Position simulateRound(final Map<Position, Pair<Direction, Character>> carts, final CharMatrix tracks)
    {
        Position crash=null;
        List<Position> currentCarts=new ArrayList<>(carts.keySet());
        currentCarts.sort((p1, p2) ->
        {
            if (p1.row==p2.row)
            {
                return Integer.compare(p1.col, p2.col);
            }
            return Integer.compare(p1.row, p2.row);
        });
        for (Position p: currentCarts)
        {
            if (' ' == tracks.at(p))
                continue;
            final Pair<Direction, Character> cart = carts.get(p);
            if (null==cart)
                continue;
            carts.remove(p);
            // move the cart, and check for a collision
            final var currentCartDir = cart.getLeft();
            Position newPos=p.updated(currentCartDir);
            if (carts.containsKey(newPos))
            {
                // when we crash, also remove the other cart
                carts.remove(newPos);
                // remember the first crash of this round
                if (null==crash)
                    crash= newPos;
                // no further simulation needed for this cart
                continue;
            }
            // on a crossing, turn the cart when needed
            Pair<Direction, Character> newCart=cart;
            char c= tracks.at(newPos);
            if ('+'==c)
            {
                char turnStyle=cart.getRight();
                if ('l'==turnStyle)
                {
                    newCart=Pair.of(currentCartDir.left(), 'c');
                }
                else if ('c'==turnStyle)
                {
                    newCart=Pair.of(currentCartDir, 'r');
                }
                else if ('r'==turnStyle)
                {
                    newCart=Pair.of(currentCartDir.right(), 'l');
                }
                else
                {
                    throw new IllegalStateException("unknown turn style "+turnStyle);
                }
            }
            // handle turns
            else if ('/'==c)
            {
                switch(currentCartDir)
                {
                    case UP -> newCart=Pair.of(RIGHT, cart.getRight());
                    case DOWN -> newCart = Pair.of(LEFT, cart.getRight());
                    case LEFT -> newCart = Pair.of(DOWN, cart.getRight());
                    case RIGHT -> newCart = Pair.of(UP, cart.getRight());
                }
            }
            else if ('\\'==c)
            {
                switch (currentCartDir)
                {
                    case UP -> newCart = Pair.of(LEFT, cart.getRight());
                    case DOWN -> newCart = Pair.of(RIGHT, cart.getRight());
                    case LEFT -> newCart = Pair.of(UP, cart.getRight());
                    case RIGHT -> newCart = Pair.of(DOWN, cart.getRight());
                }
            }

            // store the new state of the cart
            carts.put(newPos, newCart);
        }
        return crash;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix tracks = data.getLinesAsCharMatrix(' ');
        final Map<Position, Pair<Direction, Character>> carts = new HashMap<>();
        // collect the carts, and write the proper track to the map
        getCarts(tracks, carts);
        while (carts.size()>1)
        {
            simulateRound(carts, tracks);
        }

        Position left=carts.keySet().iterator().next();
        return left.col + "," + left.row;
    }
}
