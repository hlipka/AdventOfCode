package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc2019.IntCode.InputProvider;
import de.hendriklipka.aoc2019.IntCode.OutputCollector;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Day07 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day07().doPuzzle(args);
    }

    final Object syncObject = new Object();

    @Override
    protected Object solvePartA() throws IOException
    {
        if (isExample) return -1;
        final List<Integer> code = data.getLineAsInteger(",");

        List<Integer> phases = List.of(0,1,2,3,4);
        final Collection<List<Integer>> permutations = CollectionUtils.permutations(phases);
        int maxSignal=0;
        for (List<Integer> permutation : permutations)
        {
            int signal=getSignal(code, permutation);
            if (signal>maxSignal)
            {
                maxSignal=signal;
            }
        }

        return maxSignal;
    }

    private int getSignal(final List<Integer> code, final List<Integer> permutation)
    {
        IntCode a1=IntCode.fromIntList(code);
        a1.setDoInput(new InputProvider(permutation.getFirst(), 0));
        final OutputCollector oc1=new OutputCollector();
        a1.setDoOutput(oc1);
        a1.execute();

        IntCode a2=IntCode.fromIntList(code);
        a2.setDoInput(new InputProvider(permutation.get(1), oc1.getResult().getFirst()));
        final OutputCollector oc2 = new OutputCollector();
        a2.setDoOutput(oc2);
        a2.execute();

        IntCode a3=IntCode.fromIntList(code);
        a3.setDoInput(new InputProvider(permutation.get(2), oc2.getResult().getFirst()));
        final OutputCollector oc3 = new OutputCollector();
        a3.setDoOutput(oc3);
        a3.execute();

        IntCode a4=IntCode.fromIntList(code);
        a4.setDoInput(new InputProvider(permutation.get(3), oc3.getResult().getFirst()));
        final OutputCollector oc4 = new OutputCollector();
        a4.setDoOutput(oc4);
        a4.execute();

        IntCode a5=IntCode.fromIntList(code);
        a5.setDoInput(new InputProvider(permutation.get(4), oc4.getResult().getFirst()));
        final OutputCollector oc5 = new OutputCollector();
        a5.setDoOutput(oc5);
        a5.execute();

        return oc5.getResult().getFirst();
    }

    private int getSignalFromLoop(final List<Integer> code, final List<Integer> permutation)
    {
        ExecutorService e = Executors.newFixedThreadPool(11);

        IntCode a1=IntCode.fromIntList(code);
        IntCode a2=IntCode.fromIntList(code);
        IntCode a3=IntCode.fromIntList(code);
        IntCode a4=IntCode.fromIntList(code);
        IntCode a5=IntCode.fromIntList(code);

        IntCode.Pipe pipe1in=new IntCode.Pipe();
        IntCode.Pipe pipe1to2=new IntCode.Pipe();
        IntCode.Pipe pipe2to3=new IntCode.Pipe();
        IntCode.Pipe pipe3to4=new IntCode.Pipe();
        IntCode.Pipe pipe4to5=new IntCode.Pipe();

        a1.setDoInput(pipe1in);
        a2.setDoInput(pipe1to2);
        a3.setDoInput(pipe2to3);
        a4.setDoInput(pipe3to4);
        a5.setDoInput(pipe4to5);

        a1.setDoOutput(pipe1to2);
        a2.setDoOutput(pipe2to3);
        a3.setDoOutput(pipe3to4);
        a4.setDoOutput(pipe4to5);
        a5.setDoOutput(pipe1in);

        pipe1in.accept(permutation.get(0));
        pipe1to2.accept(permutation.get(1));
        pipe2to3.accept(permutation.get(2));
        pipe3to4.accept(permutation.get(3));
        pipe4to5.accept(permutation.get(4));
        pipe1in.accept(0);

        e.execute(a1::execute);
        e.execute(a2::execute);
        e.execute(a3::execute);
        e.execute(a4::execute);
        e.execute(a5::execute);

        while (true)
        {
            synchronized(syncObject)
            {
                try
                {
                    syncObject.wait(1000);
                }
                catch (InterruptedException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
            if (!a1.isFinished()) continue;
            if (!a2.isFinished()) continue;
            if (!a3.isFinished()) continue;
            if (!a4.isFinished()) continue;
            if (!a5.isFinished()) continue;
            break;
        }

        return pipe1in.getLastValue();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");

        List<Integer> phases = List.of(5,6,7,8,9);
        final Collection<List<Integer>> permutations = CollectionUtils.permutations(phases);
        int maxSignal = 0;
        System.out.println("permutations: " + permutations.size());
        int count=0;
        for (List<Integer> permutation : permutations)
        {
            if (0==count%10)
            {
                System.out.println(count);
            }
            count++;
            int signal = getSignalFromLoop(code, permutation);
            if (signal > maxSignal)
            {
                maxSignal = signal;
            }
        }

        return maxSignal;
    }
}
