package de.hendriklipka.aoc;

import java.util.List;

public enum Direction implements Keyable
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

    public List<Direction> directions()
    {
        return List.of(UP, DOWN, LEFT, RIGHT);
    }

    public Direction[] perpendicular()
    {
        return switch (this)
        {
            case UP, DOWN -> new Direction[]{LEFT, RIGHT};
            case LEFT, RIGHT -> new Direction[]{UP, DOWN};
        };
    }


    @Override
    public String getKey()
    {
        return name();
    }
}
