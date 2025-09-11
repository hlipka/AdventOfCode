package de.hendriklipka.aoc;

import java.util.List;

public enum CardinalDirection implements Keyable
{
    N, S, W, E;
    public CardinalDirection opposite()
    {
        return switch(this)
        {
            case N -> S;
            case S -> N;
            case W -> E;
            case E -> W;
        };
    }

    public CardinalDirection left()
    {
        return switch (this)
        {
            case N -> W;
            case S -> E;
            case W -> S;
            case E -> N;
        };
    }

    public CardinalDirection right()
    {
        return switch (this)
        {
            case N -> E;
            case S -> W;
            case W -> N;
            case E -> S;
        };
    }

    public List<CardinalDirection> directions()
    {
        return List.of(N, S, W, E);
    }

    public CardinalDirection[] perpendicular()
    {
        return switch (this)
        {
            case N, S -> new CardinalDirection[]{W, E};
            case W, E -> new CardinalDirection[]{N, S};
        };
    }


    @Override
    public String getKey()
    {
        return name();
    }

    public static CardinalDirection of(char c)
    {
        return valueOf(String.valueOf(c));
    }
}
