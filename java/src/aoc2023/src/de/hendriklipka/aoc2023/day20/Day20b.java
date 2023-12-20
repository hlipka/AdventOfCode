package de.hendriklipka.aoc2023.day20;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.MathUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day20b
{
    static long count=0;
    static MultiValuedMap<String, String> wires = new HashSetValuedHashMap<>();
    public static void main(String[] args)
    {
        try
        {
            Circuit c=new Circuit();
            AocParseUtils.getLines("2023", "day20").stream().map(Day20b::parseChip).forEach(chip->
            {
                c.add(chip);
                addWires(chip);
            });
            Rx rx = new Rx();
            c.add(rx);
            for (Chip chip: c.chips.values())
            {
                if (chip instanceof Conjunction)
                {
                    ((Conjunction) chip).addWires(wires.get(chip.getName()));
                }
            }
            String rxIn = wires.get("rx").iterator().next();
            System.out.println("wire into rx: " + rxIn+", needs H");
            Collection<String> rmIn = wires.get(rxIn);
            System.out.println("wires into that: "+StringUtils.join(rmIn,", "));
            c.setTracked(rmIn);
            while(true)
            {
                count++;
                c.pulse("button", "broadcast", Level.L);
                c.simulate();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void addWires(Chip chip)
    {
        String name=chip.getName();
        for (String target: chip.getTargets())
        {
            wires.put(target, name);
        }
    }

    private static Chip parseChip(String line)
    {
        return switch(line.charAt(0))
        {
            case 'b'->new Broadcast(line);
            case '%'->new FlipFlop(line);
            case '&'->new Conjunction(line);
            default -> throw new IllegalStateException("Unexpected chip: " + line);
        };
    }

    private static class Circuit
    {
        Map<String, Chip> chips = new HashMap<>();
        long _low=0;
        long _high=0;
        LinkedList<Pulse> pulses=new LinkedList<>();
        private Collection<String> tracked;
        Map<String, Long> firedFirst=new HashMap<>();
        Map<String, Long> firedPeriod=new HashMap<>();

        public void add(Chip chip)
        {
            chips.put(chip.getName(), chip);
        }

        public void pulse(String from, String to, Level level)
        {
            switch(level)
            {
                case L -> _low++;
                case H -> _high++;
            }
            Pulse pulse = new Pulse(from, to, level);
            pulses.add(pulse);
        }

        public void simulate()
        {
            while (!pulses.isEmpty())
            {
                Pulse pulse = pulses.poll();
                Chip c=chips.get(pulse.to);
                String from = pulse.from;
                if (tracked.contains(from) && pulse.level==Level.H)
                {
                    if (!firedFirst.containsKey(from))
                    {
                        System.out.println(from + " fired first at " + count);
                        firedFirst.put(from, count);
                    }
                    else {
                        Long oldPeriod=firedPeriod.get(from);
                        if (null==oldPeriod)
                        {
                            long first = firedFirst.get(from);
                            long period=count-first;
                            System.out.println("period for "+from+" is "+period);
                            firedPeriod.put(from, period);
                            if (firedPeriod.size()==4)
                            {
                                long[] periods = ArrayUtils.toPrimitive(firedPeriod.values().toArray(new Long[0]));
                                System.out.println("period is "+MathUtils.lcm(periods));
                                System.exit(1);
                            }
                        }
                    }
                }
                c.pulse(pulse, this);
            }
        }

        @Override
        public String toString()
        {
            return "Circuit{" +
                   "chips=\n" + StringUtils.join(chips.values(),"\n") +
                   "\n, _low=" + _low +
                   ", _high=" + _high +
                   '}';
        }

        public void setTracked(Collection<String> tracked)
        {
            this.tracked=tracked;
        }
    }

    private interface Chip
    {
        String getName();

        void pulse(Pulse pulse, Circuit circuit);

        String[] getTargets();
    }

    private static class Broadcast implements Chip
    {
        private final String[] targets;

        public Broadcast(String line)
        {
            targets=line.substring(line.indexOf("->")+3).split(", ");
        }

        @Override
        public String getName()
        {
            return "broadcast";
        }

        @Override
        public void pulse(Pulse pulse, Circuit circuit)
        {
            for (String t: targets)
            {
                circuit.pulse("broadcast", t, pulse.level);
            }
        }

        @Override
        public String[] getTargets()
        {
            return targets;
        }

        @Override
        public String toString()
        {
            return "Broadcast{" +
                   "targets=" + Arrays.toString(targets) +
                   '}';
        }
    }

    private static class FlipFlop implements Chip
    {
        private final String[] targets;
        private final String name;
        private Level state= Level.L;
        private long lastChange=0;
        private long lastShortChange=0;
        private long shortPeriod=0;
        private long shortChangePeriod=0;
        private long longPeriod=0;
        public FlipFlop(String line)
        {
            targets=line.substring(line.indexOf("->")+3).split(", ");
            name=line.substring(1,line.indexOf(" "));
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public void pulse(Pulse pulse, Circuit circuit)
        {
            if (pulse.level == Level.L)
            {
                switch(state)
                {
                    case L -> state= Level.H;
                    case H -> state= Level.L;
                }
                for (String t: targets)
                {
                    circuit.pulse(name, t, state);
                }
            }
        }

        @Override
        public String[] getTargets()
        {
            return targets;
        }

        @Override
        public String toString()
        {
            return "FlipFlop{" +
                   "targets=" + Arrays.toString(targets) +
                   ", name='" + name + '\'' +
                   ", state=" + state +
                   '}';
        }
    }

    private static class Conjunction implements Chip
    {
        private final String[] targets;
        private final String name;
        private Map<String, Level> _wireStates;

        public Conjunction(String line)
        {
            targets=line.substring(line.indexOf("->")+3).split(", ");
            name=line.substring(1,line.indexOf(" "));
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String[] getTargets()
        {
            return targets;
        }

        public void addWires(Collection<String> wires)
        {
            _wireStates=new HashMap<>();
            for (String wire: wires)
            {
                _wireStates.put(wire, Level.L);
            }
        }

        @Override
        public void pulse(Pulse pulse, Circuit circuit)
        {
            _wireStates.put(pulse.from, pulse.level);
            if (_wireStates.values().stream().allMatch(l-> l == Level.H))
            {
                for (String t: targets)
                {
                    circuit.pulse(name, t, Level.L);
                }
            }
            else
            {
                for (String t: targets)
                {
                    circuit.pulse(name, t, Level.H);
                }

            }
        }

        @Override
        public String toString()
        {
            return "Conjunction{" +
                   "targets=" + Arrays.toString(targets) +
                   ", name='" + name + '\'' +
                   ", _wireStates=" + _wireStates +
                   '}';
        }
    }

    private enum Level
    {
        L,H
    }

    private record Pulse(String from, String to, Level level)
    {

    }

    private static class Rx implements Chip
    {
        int _low=0;
        int _high=0;
        @Override
        public String getName()
        {
            return "rx";
        }

        @Override
        public void pulse(Pulse pulse, Circuit circuit)
        {
            switch(pulse.level)
            {
                case L -> _low++;
                case H -> _high++;
            }
        }

        @Override
        public String[] getTargets()
        {
            return new String[0];
        }
    }
}
