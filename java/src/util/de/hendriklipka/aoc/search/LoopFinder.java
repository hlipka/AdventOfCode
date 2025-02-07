package de.hendriklipka.aoc.search;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LoopFinder<T>
{
    private final Function<T, T> nextValue;
    Map<T, Long> knownValues=new HashMap<>();

    public LoopFinder(Function<T, T> nextValue)
    {
        this.nextValue=nextValue;
    }

    public T findLastValue(T startValue, long steps)
    {
        T currentValue=startValue;
        for (long i = 0; i < steps; i++)
        {
            currentValue=nextValue.apply(currentValue);
            if (knownValues.containsKey(currentValue))
            {
                final Long lastRound = knownValues.get(currentValue);
                long loopSize=i-lastRound;
                // when we found a loop, we can advance the loop index until we have only a partial loop left
                while (i+loopSize<steps)
                {
                    i+=loopSize;
                }
                // clear the know values so we calculate the rest of the loop again
                knownValues.clear();
            }
            knownValues.put(currentValue, i);
        }
        return currentValue;
    }
}
