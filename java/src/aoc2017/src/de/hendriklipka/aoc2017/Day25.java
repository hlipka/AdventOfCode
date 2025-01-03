package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day25 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day25().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<List<String>> listing = data.getStringBlocks();
        Pair<Character, Integer> state=Pair.of('A', 0);
        int steps = AocParseUtils.parseIntFromString(listing.get(0).get(1), "Perform a diagnostic checksum after (\\d+) steps.");
        listing.remove(0);
        Map<Character, State> program = parseProgram(listing);

        Map<Integer, Character> tape=new HashMap<>();

        for (int step=0;step<steps;step++)
        {
            state=simulate(state, tape, program);
        }

        return tape.values().stream().filter(c->c=='1').count();
    }

    private Pair<Character, Integer> simulate(final Pair<Character, Integer> currentState, final Map<Integer, Character> tape, final Map<Character, State> program)
    {
        State state=program.get(currentState.getLeft());
        final var currentPos = currentState.getRight();
        char current=tape.getOrDefault(currentPos, '0');
        if (current=='0')
        {
            tape.put(currentPos, state.writeWhen0);
            char nextState=state.nextStateWhen0;
            int newPos=currentPos+(state.dirWhen0=='r'?1:-1);
            return Pair.of(nextState, newPos);
        }
        tape.put(currentPos, state.writeWhen1);
        char nextState = state.nextStateWhen1;
        int newPos = currentPos + (state.dirWhen1 == 'r' ? 1 : -1);
        return Pair.of(nextState, newPos);
    }

    private Map<Character, State> parseProgram(final List<List<String>> listing)
    {
        final Map<Character, State> program = new HashMap<>();
        for (List<String> stateListing : listing)
        {
            char stateName=AocParseUtils.parseStringFromString(stateListing.get(0), "In state (.):").charAt(0);
            State state=new State();
            // all states have 'when 0' first, so we know which line is what
            state.writeWhen0= AocParseUtils.parseStringFromString(stateListing.get(2), ".*Write the value (\\d)\\.").charAt(0);
            state.dirWhen0= AocParseUtils.parseStringFromString(stateListing.get(3), ".*Move one slot to the (\\w+)\\.").charAt(0);
            state.nextStateWhen0= AocParseUtils.parseStringFromString(stateListing.get(4), ".*Continue with state (.)\\.").charAt(0);
            state.writeWhen1= AocParseUtils.parseStringFromString(stateListing.get(6), ".*Write the value (\\d)\\.").charAt(0);
            state.dirWhen1= AocParseUtils.parseStringFromString(stateListing.get(7), ".*Move one slot to the (\\w+)\\.").charAt(0);
            state.nextStateWhen1= AocParseUtils.parseStringFromString(stateListing.get(8), ".*Continue with state (.)\\.").charAt(0);
            program.put(stateName, state);
        }
        return program;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return null;
    }

    private static class State
    {
        // what to write
        char writeWhen0;
        char writeWhen1;
        // which state is next
        char nextStateWhen0;
        char nextStateWhen1;
        // how to move the cursor
        char dirWhen0;
        char dirWhen1;
    }

}
