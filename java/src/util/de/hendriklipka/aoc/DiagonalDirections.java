package de.hendriklipka.aoc;

import java.util.List;

public enum DiagonalDirections
{
    UP, DOWN, LEFT, RIGHT, RIGHT_UP, RIGHT_DOWN, LEFT_UP, LEFT_DOWN;

    public DiagonalDirections opposite()
    {
        return switch (this)
        {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case RIGHT_UP -> LEFT_DOWN;
            case RIGHT_DOWN -> LEFT_UP;
            case LEFT_UP -> RIGHT_DOWN;
            case LEFT_DOWN -> RIGHT_UP;
        };
    }

    public DiagonalDirections left()
    {
        return switch (this)
        {
            case UP -> LEFT_UP;
            case DOWN -> RIGHT_UP;
            case LEFT -> LEFT_DOWN;
            case RIGHT -> RIGHT_UP;
            case RIGHT_UP -> UP;
            case RIGHT_DOWN -> RIGHT;
            case LEFT_UP -> LEFT;
            case LEFT_DOWN -> DOWN;
        };
    }

    public DiagonalDirections right()
    {
        return switch (this)
        {
            case UP -> RIGHT_UP;
            case DOWN -> LEFT_DOWN;
            case LEFT -> LEFT_UP;
            case RIGHT -> RIGHT_DOWN;
            case RIGHT_UP -> RIGHT;
            case RIGHT_DOWN -> DOWN;
            case LEFT_UP -> UP;
            case LEFT_DOWN -> LEFT;
        };
    }

    public static List<DiagonalDirections> directions()
    {
        return List.of(UP, DOWN, LEFT, RIGHT, RIGHT_UP, RIGHT_DOWN, LEFT_UP, LEFT_DOWN);
    }
}
