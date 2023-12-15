package de.hendriklipka.aoc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CycleFinder<T>
{
    private final Function<T, T> _advance;
    private final long _loops;

    public CycleFinder(Function<T, T> advance, long loopCount)
    {
        _advance=advance;
        _loops=loopCount;
    }

    public T getFinalState(T initialState)
    {
        T state=initialState;
        Map<T, Integer> states = new HashMap<>();
        int count=0;
        int loopSize;
        while (true)
        {
            states.put(state, count);
            count++;
            T newState=_advance.apply(state);
            int oldCount = states.getOrDefault(newState, -1);
            if (-1!=oldCount)
            {
                loopSize=count-oldCount;
                break;
            }
            state=newState;
        }
        while(count+loopSize<_loops)
        {
            count+=loopSize;
        }
        while(count<_loops+1)
        {
            state=_advance.apply(state);
            count++;
        }
        return state;
    }
}
