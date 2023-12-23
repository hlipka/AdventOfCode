package de.hendriklipka.aoc;

public enum Direction
{
    UP, DOWN, LEFT, RIGHT;
    public Direction opposite()
    {
        return switch(this)
        {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public Direction left()
    {
        return switch (this)
        {
            case UP -> LEFT;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    public Direction right()
    {
        return switch (this)
        {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
        };
    }
}
