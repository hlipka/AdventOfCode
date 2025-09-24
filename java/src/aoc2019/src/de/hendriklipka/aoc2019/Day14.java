package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Day14 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day14().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return getOreAmount(BigInteger.ONE);
    }

    private BigInteger getOreAmount(final BigInteger fuelAmount) throws IOException
    {
        Map<String, Material> materials = new HashMap<>();
        materials.put("ORE", new Material("ORE", BigInteger.ONE));
        final List<String> config = data.getLines();
        for (String line : config)
        {
            Material m = readMaterial(line);
            materials.put(m._name, m);
        }
        for (String line: config)
        {
            configureNeeds(line, materials);
        }
        Material fuel=materials.get("FUEL");
        fuel._needed= fuelAmount;
        calculateNeeds(new ArrayList<>(List.of(fuel)), materials);

        return materials.get("ORE")._needed;
    }

    private void calculateNeeds(final List<Material> toCalculate, final Map<String, Material> materials)
    {
        while (!toCalculate.isEmpty())
        {
            Material m = toCalculate.getFirst();
            toCalculate.removeFirst();
            if (m._name.equals("ORE"))
                continue;
            // when we still are needed by something else, add it to the end of the list again so we can look at other materials
            if (!m._neededBy.isEmpty())
            {
                toCalculate.add(m);
                continue;
            }
            // remove all further occurrences of this material from the list so we don't calculate it multiple times
            while (toCalculate.remove(m));

            // calculate how much we must _produce_  (according to the multiples)
            // we need 5, but the multiple is 7
            if (!BigInteger.ZERO.equals(m._needed.mod(m._multiple)))
            {
                m._needed = m._needed.add(m._multiple.subtract((m._needed.mod(m._multiple))));
            }
            BigInteger prodAmount=m._needed.divide(m._multiple);
            for (Map.Entry<String, BigInteger> needs : m._materials.entrySet())
            {
                Material needMaterial = materials.get(needs.getKey());
                // increase the production amount of this material
                needMaterial.addNeeded(needs.getValue().multiply(prodAmount));
                // make sure we will look at it
                toCalculate.add(needMaterial);
                // and remove ourselves, since our needs are covered
                needMaterial._neededBy.remove(m._name);
            }
        }
    }

    private void configureNeeds(final String line, final Map<String, Material> materials)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line, "(.*)=> \\d+ (\\w+)");
        final var targetMaterialName = parts.get(1);
        Material m=materials.get(targetMaterialName);
        String[] needs=parts.get(0).split(",");
        for (String needStr: needs)
        {
            needStr=needStr.trim();
            parts = AocParseUtils.parsePartsFromString(needStr, "(\\d+) (\\w+)");
            final var neededName = parts.get(1);
            Material needMaterial = materials.get(neededName);
            m.addNeed(neededName, new BigInteger(parts.get(0)));
            needMaterial.addNeededBy(targetMaterialName);
        }
    }

    private Material readMaterial(final String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line, ".*=> (\\d+) (\\w+)");
        return new Material(parts.get(1), new BigInteger(parts.get(0)));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        BigInteger maxOre=new BigInteger("1000000000000");
        BigInteger upperFuel=new BigInteger("10");
        BigInteger lowerFuel=new BigInteger("10");
        // find a range where we will find our result
        while (getOreAmount(upperFuel).compareTo(maxOre) < 0)
        {
            lowerFuel=upperFuel;
            upperFuel =upperFuel.multiply(BigInteger.TWO);
        }
        // do a binary search in this range to find the right value
        while(true)
        {
            if (upperFuel.subtract(lowerFuel).equals(BigInteger.ONE))
            {
                return lowerFuel;
            }
            BigInteger middleFuel=lowerFuel.add(upperFuel).divide(BigInteger.TWO);
            BigInteger currentOre = getOreAmount(middleFuel);
            if (currentOre.compareTo(maxOre)<0) // we must be in the upper half
            {
                lowerFuel=middleFuel;
            }
            else
            {
                upperFuel=middleFuel;
            }
        }
    }

    private static class Material
    {
        String _name;
        BigInteger _multiple;
        BigInteger _needed = BigInteger.ZERO;
        Map<String, BigInteger> _materials = new HashMap<>();
        Set<String> _neededBy=new HashSet<>();

        public Material(String material, BigInteger multiple)
        {
            this._name = material;
            this._multiple = multiple;
        }

        void addNeed(String material, BigInteger amount)
        {
            _materials.put(material, amount);
        }

        void addNeededBy(String material)
        {
            _neededBy.add(material);
        }

        void addNeeded(BigInteger amount)
        {
            _needed=_needed.add(amount);
        }

        @Override
        public String toString()
        {
            return "Material{" +
                   "_name='" + _name + '\'' +
                   ", _multiple=" + _multiple +
                   '}';
        }
    }
}
