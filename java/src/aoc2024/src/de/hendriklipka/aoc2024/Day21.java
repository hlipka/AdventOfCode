package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;

import java.io.IOException;
import java.util.*;

import static de.hendriklipka.aoc.Direction.*;

public class Day21 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day21().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> codes = data.getLines();
        long sum=0;
        for (String code : codes)
        {
            // for the first part run both ways so we can compare
            long presses=getPresses2(code, 2);
            long pressesOld=getPresses(code);
            System.out.println("presses for " + code + "->" + presses + "/" + pressesOld);
            long num=Long.parseLong(code.substring(0,3));
            sum+=presses*num;
        }
        return sum;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> codes = data.getLines();
        long sum = 0;
        for (String code : codes)
        {
            long presses = getPresses2(code, 25);
            long num = Long.parseLong(code.substring(0, 3));
            sum += presses * num;
            System.out.println("presses for " + code + "->" + presses);
        }
        return sum;
    }

    // the 'brute force' solution - expand all button presses down all the key pads
    private int getPresses(final String code)
    {
        List<String> numPadMoves = findMovesOnNumPad(code, 0, 'A');
        // now expand to the first keypad
        for (int i = 0; i < 2; i++)
        {
            List<String> newMoves = new ArrayList<>();
            for (String move : numPadMoves)
                newMoves.addAll(findMovesOnDirPad(move, 'A'));
            numPadMoves=newMoves;
        }

        // once we have button sequences lok for the shortest one
        int presses=Integer.MAX_VALUE;
        for (String move: numPadMoves)
        {
            int len=move.length();
            if (len<presses)
                presses=len;
        }
        return presses;
    }

    private List<String> findMovesOnDirPad(final String dirPadMove, char previousState)
    {
        if (dirPadMove.isEmpty())
        {
            return List.of("");
        }
        final List<String> result = new ArrayList<>();
        final var currentTarget = dirPadMove.charAt(0);
        if (previousState == currentTarget)
        {
            String moveStr="A";
            List<String> restMoves = findMovesOnDirPad(dirPadMove.substring(1), currentTarget);
            for (String restMove : restMoves)
            {
                result.add(moveStr + restMove);
            }
            return result;
        }
        // these are the potential moves to get the robot to move from the previous button to the new one
        final List<List<Direction>> movesForButton = MOVES_DIRPAD.get("" + previousState + currentTarget);
        for (List<Direction> move : movesForButton)
        {
            StringBuilder moveStr = new StringBuilder();
            for (Direction direction : move)
            {
                moveStr.append(direction.name().charAt(0));
            }
            // we need to submit the number
            moveStr.append("A");
            // recursively add the rest of the code
            List<String> restMoves = findMovesOnDirPad(dirPadMove.substring(1),currentTarget);
            for (String restMove : restMoves)
            {
                result.add(moveStr.toString()+restMove);
            }
        }
        return result;
    }

    // this is the actual recursive solution
    private long getPresses2(final String code, int depth)
    {
        // we still expand the num pad presses directly, there aren't that many combinations
        List<String> numPadMoves = findMovesOnNumPad(code, 0, 'A');
        long bestLen = Long.MAX_VALUE;
        // once we have them, recursively find out how many human button presses they need in the best case
        for (String move : numPadMoves)
        {
            // go through the moves from left to right
            // for each single move, find the best expansion to move from the previous state to the current state
            // sum them up, and we might have a better final length
            char lastButton = 'A';
            long len = 0;
            for (char m : move.toCharArray())
            {
                len += findMovesOnDirPad2(lastButton, m, depth);
                lastButton = m;
            }
            if (len < bestLen)
                bestLen = len;
        }
        ;
        return bestLen;
    }

    private final Map<String, Long> memoize=new HashMap<>();

    private long findMovesOnDirPad2(char from, final char to, int depth)
    {
        // we are the last keypad, which always is just one press (by the human)
        if (depth==0)
        {
            return 1;
        }

        // when we are at the right position, we already did press the correct button once, so all subsequent robots are at the right position to press it again
        if (from == to)
        {
            return 1;
        }

        String key=""+from+to+"-"+depth;
        Long l=memoize.get(key);
        if (null!=l)
            return l;

        long bestLen = Long.MAX_VALUE;

        // these are the potential moves to get the robot to move from the previous button to the new one
        final List<List<Direction>> movesForButton = MOVES_DIRPAD.get("" + from + to);
        for (List<Direction> move : movesForButton)
        {
            long len=0;
            char last='A'; // on each level we start at 'A' because we did press a button before
            for (Direction direction : move)
            {
                // keep track of the last state, and find the best sequence for the next button
                char next=direction.name().charAt(0);
                len += findMovesOnDirPad2(last, next, depth - 1);
                last=next;
            }
            // at the end, we need to press 'A'
            len+= findMovesOnDirPad2(last, 'A', depth - 1);
            if (len<bestLen)
                bestLen=len;
        }
        memoize.put(key, bestLen);
        return bestLen;
    }

    private List<String> findMovesOnNumPad(final String code, final int charNum, final char previousButton)
    {
        if (charNum==code.length())
        {
            return List.of("");
        }
        final List<String> result = new ArrayList<>();
        // these are the potential moves to get the robot to move from the previous button to the new one
        final List<List<Direction>> movesForButton = MOVES_NUMPAD.get("" + previousButton + code.charAt(charNum));
        for (List<Direction> move : movesForButton)
        {
            StringBuilder moveStr= new StringBuilder();
            for (Direction direction : move)
            {
                moveStr.append(direction.name().charAt(0));
            }
            // we need to submit the number
            moveStr.append("A");
            // recursively add the rest of the code
            List<String> restMoves= findMovesOnNumPad(code, charNum + 1, code.charAt(charNum));
            for (String restMove: restMoves)
            {
                result.add(moveStr + restMove);
            }
        }
        return result;
    }

    // define all the potential moves for the num keypad
    // the absolute distance is always the manhattan distance
    // so any direction changes make the move more expensive
    // therefore the optimal moves are always one horizontal and one vertical
    // so we store are allowed / useful combinations
    final static Map<String, List<List<Direction>>> MOVES_NUMPAD =new HashMap<>();
    static
    {
        MOVES_NUMPAD.put("A0", List.of(List.of(LEFT)));
        MOVES_NUMPAD.put("A1", List.of(List.of(UP, LEFT, LEFT)));
        MOVES_NUMPAD.put("A2", List.of(List.of(UP, LEFT), List.of(LEFT, UP)));
        MOVES_NUMPAD.put("A3", List.of(List.of(UP)));
        MOVES_NUMPAD.put("A4", List.of(List.of(UP, UP, LEFT, LEFT)));
        MOVES_NUMPAD.put("A5", List.of(List.of(UP, UP, LEFT), List.of(LEFT, UP, UP)));
        MOVES_NUMPAD.put("A6", List.of(List.of(UP, UP)));
        MOVES_NUMPAD.put("A7", List.of(List.of(UP, UP, UP, LEFT, LEFT)));
        MOVES_NUMPAD.put("A8", List.of(List.of(UP, UP, UP, LEFT), List.of(LEFT, UP, UP, UP)));
        MOVES_NUMPAD.put("A9", List.of(List.of(UP, UP, UP)));

        MOVES_NUMPAD.put("0A", List.of(List.of(RIGHT)));
        MOVES_NUMPAD.put("01", List.of(List.of(UP, LEFT)));
        MOVES_NUMPAD.put("02", List.of(List.of(UP)));
        MOVES_NUMPAD.put("03", List.of(List.of(UP, RIGHT), List.of(RIGHT, UP)));
        MOVES_NUMPAD.put("04", List.of(List.of(UP, UP, LEFT)));
        MOVES_NUMPAD.put("05", List.of(List.of(UP, UP)));
        MOVES_NUMPAD.put("06", List.of(List.of(UP, UP, RIGHT), List.of(RIGHT, UP, UP)));
        MOVES_NUMPAD.put("07", List.of(List.of(UP, UP, UP, LEFT)));
        MOVES_NUMPAD.put("08", List.of(List.of(UP, UP, UP)));
        MOVES_NUMPAD.put("09", List.of(List.of(UP, UP, UP, RIGHT), List.of(RIGHT, UP, UP, UP)));

        MOVES_NUMPAD.put("1A", List.of(List.of(RIGHT, RIGHT, DOWN)));
        MOVES_NUMPAD.put("10", List.of(List.of(RIGHT, DOWN)));
        MOVES_NUMPAD.put("12", List.of(List.of(RIGHT)));
        MOVES_NUMPAD.put("13", List.of(List.of(RIGHT, RIGHT)));
        MOVES_NUMPAD.put("14", List.of(List.of(UP)));
        MOVES_NUMPAD.put("15", List.of(List.of(UP, RIGHT), List.of(RIGHT, UP)));
        MOVES_NUMPAD.put("16", List.of(List.of(UP, RIGHT, RIGHT), List.of(RIGHT, RIGHT, UP)));
        MOVES_NUMPAD.put("17", List.of(List.of(UP, UP)));
        MOVES_NUMPAD.put("18", List.of(List.of(UP, UP, RIGHT), List.of(RIGHT, UP, UP)));
        MOVES_NUMPAD.put("19", List.of(List.of(UP, UP, RIGHT, RIGHT), List.of(RIGHT, RIGHT, UP, UP)));

        MOVES_NUMPAD.put("2A", List.of(List.of(DOWN, RIGHT), List.of(RIGHT, DOWN)));
        MOVES_NUMPAD.put("20", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("21", List.of(List.of(LEFT)));
        MOVES_NUMPAD.put("23", List.of(List.of(RIGHT)));
        MOVES_NUMPAD.put("24", List.of(List.of(LEFT, UP), List.of(UP, LEFT)));
        MOVES_NUMPAD.put("25", List.of(List.of(UP)));
        MOVES_NUMPAD.put("26", List.of(List.of(RIGHT, UP), List.of(UP, RIGHT)));
        MOVES_NUMPAD.put("27", List.of(List.of(LEFT, UP, UP), List.of(UP, UP, LEFT)));
        MOVES_NUMPAD.put("28", List.of(List.of(UP, UP)));
        MOVES_NUMPAD.put("29", List.of(List.of(RIGHT, UP, UP), List.of(UP, UP, RIGHT)));

        MOVES_NUMPAD.put("3A", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("30", List.of(List.of(LEFT, DOWN), List.of(DOWN, LEFT)));
        MOVES_NUMPAD.put("31", List.of(List.of(LEFT, LEFT)));
        MOVES_NUMPAD.put("32", List.of(List.of(LEFT)));
        MOVES_NUMPAD.put("34", List.of(List.of(UP, LEFT, LEFT), List.of(LEFT, LEFT, UP)));
        MOVES_NUMPAD.put("35", List.of(List.of(UP, LEFT), List.of(LEFT, UP)));
        MOVES_NUMPAD.put("36", List.of(List.of(UP)));
        MOVES_NUMPAD.put("37", List.of(List.of(UP, UP, LEFT, LEFT), List.of(LEFT, LEFT, UP, UP)));
        MOVES_NUMPAD.put("38", List.of(List.of(UP, UP, LEFT), List.of(LEFT, UP, UP)));
        MOVES_NUMPAD.put("39", List.of(List.of(UP, UP)));

        MOVES_NUMPAD.put("4A", List.of(List.of(RIGHT, RIGHT, DOWN, DOWN)));
        MOVES_NUMPAD.put("40", List.of(List.of(RIGHT, DOWN, DOWN)));
        MOVES_NUMPAD.put("41", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("42", List.of(List.of(DOWN, RIGHT), List.of(RIGHT, DOWN)));
        MOVES_NUMPAD.put("43", List.of(List.of(RIGHT, RIGHT, DOWN), List.of(DOWN, RIGHT, RIGHT)));
        MOVES_NUMPAD.put("45", List.of(List.of(RIGHT)));
        MOVES_NUMPAD.put("46", List.of(List.of(RIGHT, RIGHT)));
        MOVES_NUMPAD.put("47", List.of(List.of(UP)));
        MOVES_NUMPAD.put("48", List.of(List.of(RIGHT, UP), List.of(UP, RIGHT)));
        MOVES_NUMPAD.put("49", List.of(List.of(RIGHT, RIGHT, UP), List.of(UP, RIGHT, RIGHT)));

        MOVES_NUMPAD.put("5A", List.of(List.of(DOWN, DOWN, RIGHT), List.of(RIGHT, DOWN, DOWN)));
        MOVES_NUMPAD.put("50", List.of(List.of(DOWN, DOWN)));
        MOVES_NUMPAD.put("51", List.of(List.of(DOWN, LEFT), List.of(LEFT, DOWN)));
        MOVES_NUMPAD.put("52", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("53", List.of(List.of(DOWN, RIGHT), List.of(RIGHT, DOWN)));
        MOVES_NUMPAD.put("54", List.of(List.of(LEFT)));
        MOVES_NUMPAD.put("56", List.of(List.of(RIGHT)));
        MOVES_NUMPAD.put("57", List.of(List.of(UP, LEFT), List.of(LEFT, UP)));
        MOVES_NUMPAD.put("58", List.of(List.of(UP)));
        MOVES_NUMPAD.put("59", List.of(List.of(UP, RIGHT), List.of(RIGHT, UP)));

        MOVES_NUMPAD.put("6A", List.of(List.of(DOWN, DOWN)));
        MOVES_NUMPAD.put("60", List.of(List.of(LEFT, DOWN, DOWN), List.of(DOWN, DOWN, LEFT)));
        MOVES_NUMPAD.put("61", List.of(List.of(LEFT, LEFT, DOWN), List.of(DOWN, LEFT, LEFT)));
        MOVES_NUMPAD.put("62", List.of(List.of(LEFT, DOWN), List.of(DOWN, LEFT)));
        MOVES_NUMPAD.put("63", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("64", List.of(List.of(LEFT, LEFT)));
        MOVES_NUMPAD.put("65", List.of(List.of(LEFT)));
        MOVES_NUMPAD.put("67", List.of(List.of(LEFT, LEFT, UP), List.of(UP, LEFT, LEFT)));
        MOVES_NUMPAD.put("68", List.of(List.of(LEFT, UP), List.of(UP, LEFT)));
        MOVES_NUMPAD.put("69", List.of(List.of(UP)));

        MOVES_NUMPAD.put("7A", List.of(List.of(RIGHT, RIGHT, DOWN, DOWN, DOWN)));
        MOVES_NUMPAD.put("70", List.of(List.of(RIGHT, DOWN, DOWN, DOWN)));
        MOVES_NUMPAD.put("71", List.of(List.of(DOWN, DOWN)));
        MOVES_NUMPAD.put("72", List.of(List.of(DOWN, DOWN, RIGHT), List.of(RIGHT, DOWN, DOWN)));
        MOVES_NUMPAD.put("73", List.of(List.of(DOWN, DOWN, RIGHT, RIGHT), List.of(RIGHT, RIGHT, DOWN, DOWN)));
        MOVES_NUMPAD.put("74", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("75", List.of(List.of(DOWN, RIGHT), List.of(RIGHT, DOWN)));
        MOVES_NUMPAD.put("76", List.of(List.of(DOWN, RIGHT, RIGHT), List.of(RIGHT, RIGHT, DOWN)));
        MOVES_NUMPAD.put("78", List.of(List.of(RIGHT)));
        MOVES_NUMPAD.put("79", List.of(List.of(RIGHT, RIGHT)));

        MOVES_NUMPAD.put("8A", List.of(List.of(DOWN, DOWN, DOWN, RIGHT), List.of(RIGHT, DOWN, DOWN, DOWN)));
        MOVES_NUMPAD.put("80", List.of(List.of(DOWN, DOWN, DOWN)));
        MOVES_NUMPAD.put("81", List.of(List.of(LEFT, DOWN, DOWN), List.of(DOWN, DOWN, LEFT)));
        MOVES_NUMPAD.put("82", List.of(List.of(DOWN, DOWN)));
        MOVES_NUMPAD.put("83", List.of(List.of(RIGHT, DOWN, DOWN), List.of(DOWN, DOWN, RIGHT)));
        MOVES_NUMPAD.put("84", List.of(List.of(LEFT, DOWN), List.of(DOWN, LEFT)));
        MOVES_NUMPAD.put("85", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("86", List.of(List.of(RIGHT, DOWN), List.of(DOWN, RIGHT)));
        MOVES_NUMPAD.put("87", List.of(List.of(LEFT)));
        MOVES_NUMPAD.put("89", List.of(List.of(RIGHT)));

        MOVES_NUMPAD.put("9A", List.of(List.of(DOWN, DOWN, DOWN)));
        MOVES_NUMPAD.put("90", List.of(List.of(LEFT, DOWN, DOWN, DOWN), List.of(DOWN, DOWN, DOWN, LEFT)));
        MOVES_NUMPAD.put("91", List.of(List.of(DOWN, DOWN, LEFT, LEFT), List.of(LEFT, LEFT, DOWN, DOWN)));
        MOVES_NUMPAD.put("92", List.of(List.of(DOWN, DOWN, LEFT), List.of(LEFT, DOWN, DOWN)));
        MOVES_NUMPAD.put("93", List.of(List.of(DOWN, DOWN)));
        MOVES_NUMPAD.put("94", List.of(List.of(DOWN, LEFT, LEFT), List.of(LEFT, LEFT, DOWN)));
        MOVES_NUMPAD.put("95", List.of(List.of(DOWN, LEFT), List.of(LEFT, DOWN)));
        MOVES_NUMPAD.put("96", List.of(List.of(DOWN)));
        MOVES_NUMPAD.put("97", List.of(List.of(LEFT, LEFT)));
        MOVES_NUMPAD.put("98", List.of(List.of(LEFT)));

    }

    // these are the instructions to move a robot on a directional keypad
    // we also need all combinations since the upper robots will also keep a state
    final static Map<String, List<List<Direction>>> MOVES_DIRPAD = new HashMap<>();
    static
    {
        MOVES_DIRPAD.put("AU", List.of(List.of(LEFT)));
        MOVES_DIRPAD.put("AD", List.of(List.of(LEFT, DOWN), List.of(DOWN, LEFT)));
        MOVES_DIRPAD.put("AL", List.of(List.of(DOWN, LEFT, LEFT), List.of(LEFT, DOWN, LEFT)));
        MOVES_DIRPAD.put("AR", List.of(List.of(DOWN)));

        MOVES_DIRPAD.put("UA", List.of(List.of(RIGHT)));
        MOVES_DIRPAD.put("UD", List.of(List.of(DOWN)));
        MOVES_DIRPAD.put("UL", List.of(List.of(DOWN, LEFT)));
        MOVES_DIRPAD.put("UR", List.of(List.of(DOWN, RIGHT), List.of(RIGHT, DOWN)));

        MOVES_DIRPAD.put("DA", List.of(List.of(UP, RIGHT), List.of(RIGHT, UP)));
        MOVES_DIRPAD.put("DU", List.of(List.of(UP)));
        MOVES_DIRPAD.put("DL", List.of(List.of(LEFT)));
        MOVES_DIRPAD.put("DR", List.of(List.of(RIGHT)));

        MOVES_DIRPAD.put("LA", List.of(List.of(RIGHT, RIGHT, UP), List.of(RIGHT, UP, RIGHT)));
        MOVES_DIRPAD.put("LU", List.of(List.of(RIGHT, UP)));
        MOVES_DIRPAD.put("LD", List.of(List.of(RIGHT)));
        MOVES_DIRPAD.put("LR", List.of(List.of(RIGHT, RIGHT)));

        MOVES_DIRPAD.put("RA", List.of(List.of(UP)));
        MOVES_DIRPAD.put("RU", List.of(List.of(LEFT, UP), List.of(UP, LEFT)));
        MOVES_DIRPAD.put("RD", List.of(List.of(LEFT)));
        MOVES_DIRPAD.put("RL", List.of(List.of(LEFT, LEFT)));
    }
}
