package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day24 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day24().doPuzzle(args);
    }

    Map<String, String> swapTable;
    @Override
    protected Object solvePartA() throws IOException
    {
        swapTable = new HashMap<>(); // this is empty for part A
        final List<List<String>> config = data.getStringBlocks();
        Map<String, Integer> wires = new HashMap<>();

        for (String wireData: config.get(0))
        {
            final List<String> parts = AocParseUtils.getGroupsFromLine(wireData, "(\\w+): (\\d)");
            wires.put(parts.get(0), Integer.parseInt(parts.get(1)));
        }

        Set<Gate> gates = new HashSet<>();

        for (String gateData: config.get(1))
        {
            gates.add(createGate(gateData));
        }
        while (!gates.isEmpty())
        {
            for (Gate gate : gates)
            {
                if (gate.canRun(wires))
                {
                    gate.calculate(wires);
                    gates.remove(gate);
                    break;
                }
            }
        }
        return getOutput(wires);
    }

    private long getOutput(final Map<String, Integer> wires)
    {
        long result=0;
        long i=0;
        long pot=1;
        while (true)
        {
            final String format = "z"+getNumber(i);
            if (!wires.containsKey(format)) break;
            long w=wires.get(format);
            result+=w*pot;
            i++;
            pot*=2;
        }
        return result;
    }

    private static String getNumber(final long i)
    {
        return String.format("%02d", i);
    }

    private String swapWire(String wire)
    {
        if (!swapTable.containsKey(wire))
            return wire;
        return swapTable.get(wire);
    }

    private void renameWire(final Set<Gate> gates, String oldWire, String newWire)
    {
        System.out.println("rename " + oldWire + " to " + newWire);
        gates.forEach(g->g.renameWire(oldWire, newWire));
    }

    private Gate createGate(final String gateData)
    {
        List<String> parts = AocParseUtils.getGroupsFromLine(gateData, "(\\w+) (\\w+) (\\w+) -> (\\w+)");
        return switch (parts.get(1))
        {
            case "AND" -> new And(parts.get(0), parts.get(2), swapWire(parts.get(3)));
            case "OR" -> new Or(parts.get(0), parts.get(2), swapWire(parts.get(3)));
            case "XOR" -> new Xor(parts.get(0), parts.get(2), swapWire(parts.get(3)));
            default -> null;
        };
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        if (isExample)
            return -1;
        // so we know that '00' implements an half adder, with carry-out being 'mcg'
        // so we can walk through the input signals, and check for wiring:
        // - x_n+y_n must be connected by an XOR (which results in 'si_n') and an AND (which results in 'ci_n')
        // - next, the previous carry_out (which also is our carry-in) must be connected with 'si_n' by an XOR, which results in 'cout_n'
        // - the same signals are also connected by an AND, resulting in 'cs_n'
        // - finally, 'ci_n' and 'cs_n' are ORed together to get 'cout_n'

        // we run this through, see what mis-matches we can, and add these to the swap table ´(see below)
        // we do these corrections until we do not find any problems anymore

        // we have a manual replacement table we use while reading the inputs
        // this able should have 4 pairs once we can verify the whole schematic to be correct
        swapTable=Map.of(
                "shh","z21"
                ,"z21","shh"
                ,"dtk","vgs"
                ,"vgs","dtk"
                ,"z33","dqr"
                ,"dqr","z33"
                ,"z39","pfw"
                ,"pfw","z39"
        );

        final List<List<String>> config = data.getStringBlocks();

        Set<Gate> gates = new HashSet<>();

        for (String gateData: config.get(1))
        {
            gates.add(createGate(gateData));
        }

        renameWire(gates, "mcg", "co00");

        for (int i=1;i<45;i++)
        {
            String num=getNumber(i);
            Gate g = getGateFor(gates, "z"+num);
            if (!g.getType().equals("XOR"))
            {
                System.out.println("sum output gate for "+i+" is not XOR: "+g);
                return -1;
            }
        }
        // TODO gate for z45 is OR (because it is the carry out of stage 44)

        for (int i=1;i<45;i++)
        {
            String num=getNumber(i);
            Gate g1=findGate(gates, "x"+num, "y"+num, "XOR");
            if (g1==null)
            {
                throw new IllegalStateException("cannot find input XOR for "+i);
            }
            renameWire(gates, g1.getOut(), "si"+num);
            Gate g2=findGate(gates, "x"+num, "y"+num, "AND");
            if (g2==null)
            {
                throw new IllegalStateException("cannot find input AND for "+i);
            }
            renameWire(gates, g2.getOut(), "ci"+num);
        }
        for (int i=1;i<45;i++)
        {
            String num = getNumber(i);
            String pNum = getNumber(i-1);
            Gate g1=findGate(gates, "si"+num, "co"+pNum, "XOR");
            if (g1==null)
            {
                System.out.println("cannot find sum output XOR for "+i);
                gates.stream().filter(g->g.matchesOneWire("si"+num, "co"+pNum)).forEach(g-> System.out.println(g));
                return -1;
            }
            if (!g1.getOut().equals("z"+num))
            {
                throw new IllegalStateException("sum output XOR for "+i+" does go to "+g1.getOut()+" instead: "+g1);
            }
            Gate g2=findGate(gates, "si"+num, "co"+pNum, "AND");
            if (g2==null)
            {
                throw new IllegalStateException("cannot find carry input AND for "+i);
            }
            renameWire(gates, g2.getOut(), "cs"+num);

            Gate g3=findGate(gates, "ci"+num, "cs"+num, "OR");
            if (g3==null)
            {
                System.out.println("cannot find carry output OR for "+i);
                gates.stream().filter(g->g.matchesOneWire("ci"+num, "cs"+num)).forEach(g-> System.out.println(g));
                return -1;
            }
            renameWire(gates, g3.getOut(), "co"+num);
        }

        List<String> swaps = new ArrayList<>(swapTable.keySet());
        swaps.sort(String::compareTo);
        return StringUtils.join(swaps,",");
    }

    private Gate getGateFor(final Set<Gate> gates, final String output)
    {
        return gates.stream().filter(g->g.getOut().equals(output)).findFirst().orElse(null);
    }

    private Gate findGate(final Set<Gate> gates, final String wire1, final String wire2, final String type)
    {
        return gates.stream().filter(g-> g.getType().equals(type) && g.matchesWire(wire1) && g.matchesWire(wire2)).findFirst().orElse(null);
    }

    private static abstract class Gate
    {
        String _wire1, _wire2, _out;
        public Gate(String wire1, String wire2, final String out)
        {
            _wire1 = wire1;
            _wire2 = wire2;
            _out=out;
        }

        public boolean canRun(final Map<String, Integer> wires)
        {
            return wires.containsKey(_wire1) && wires.containsKey(_wire2);
        }

        public void renameWire(String from, String to)
        {
            if (_wire1.equals(from))
                _wire1 = to;
            if (_wire2.equals(from))
                _wire2 = to;
            if (_out.equals(from))
                _out = to;
        }
        abstract void calculate(Map<String, Integer> wires);
        boolean matchesWire(String wire)
        {
            return (_wire1.equals(wire) || _wire2.equals(wire));
        }

        String getOut()
        {
            return _out;
        }

        abstract String getType();

        @Override
        public String toString()
        {
            return "Gate{" +
                   _wire1 +" "+ getType()+" " +
                    _wire2 + " -> " + _out  +
                   '}';
        }

        public boolean matchesOneWire(final String w1, final String w2)
        {
            return
                    _wire1.equals(w1)
                    || _wire1.equals(w2)
                    || _wire2.equals(w1)
                    || _wire2.equals(w2)
                    ;
        }
    }

    private static class And extends Gate
    {
        public And(final String wire1, final String wire2, final String out)
        {
            super(wire1, wire2, out);
        }

        @Override
        public void calculate(final Map<String, Integer> wires)
        {
            wires.put(_out, wires.get(_wire1) & wires.get(_wire2));
        }

        @Override
        java.lang.String getType()
        {
            return "AND";
        }
    }

    private static class Or extends Gate
    {
        public Or(final String wire1, final String wire2, final String out)
        {
            super(wire1, wire2, out);
        }

        @Override
        public void calculate(final Map<String, Integer> wires)
        {
            wires.put(_out, wires.get(_wire1) | wires.get(_wire2));
        }

        @Override
        java.lang.String getType()
        {
            return "OR";
        }
    }

    private static class Xor extends Gate
    {
        public Xor(final String wire1, final String wire2, final String out)
        {
            super(wire1, wire2, out);
        }

        @Override
        public void calculate(final Map<String, Integer> wires)
        {
            wires.put(_out, wires.get(_wire1) ^ wires.get(_wire2));
        }

        @Override
        java.lang.String getType()
        {
            return "XOR";
        }
    }
}
