package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocCollectionUtils;
import de.hendriklipka.aoc.AocDataFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hli
 */
public class Day08a
{
    public static void main(String[] args)
    {
        try
        {
            final List<Instruction> instructions = AocDataFileUtils.getLines("2017", "day08").stream().map(Day08a::parseLine).toList();
            Map<String, Integer> registers = new HashMap<>();

            for (Instruction instruction : instructions)
            {
                String target = instruction.getTarget();
                if (instruction.mustRun(registers))
                {
                    if (instruction.getOperation().equals("inc"))
                    {
                        registers.put(target, registers.getOrDefault(target, 0) + instruction.getValue());
                    }
                    else
                    {
                        registers.put(target, registers.getOrDefault(target, 0) - instruction.getValue());
                    }
                }
            }
            final var regValues = registers.values().stream().toList();
            System.out.println(regValues.get(AocCollectionUtils.findLargestElement(regValues)));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Instruction parseLine(String line)
    {
        final String[] parts = StringUtils.split(line, " ");
        return new Instruction(parts[0], parts[1], parts[2], parts[4], parts[5], parts[6]);
    }

    private static class Instruction
    {
        private final String _target;
        private final String _operation;
        private final String _value;
        private final String _op1;
        private final String _condition;
        private final String _op2;

        public Instruction(final String target, final String operation, final String value, final String op1, final String condition,
                           final String op2)
        {
            _target = target;
            _operation = operation;
            _value = value;
            _op1 = op1;
            _condition = condition;
            _op2 = op2;
        }

        public Integer getValue()
        {
            return Integer.parseInt(_value);
        }

        public String getOperation()
        {
            return _operation;
        }

        public String getTarget()
        {
            return _target;
        }

        public boolean mustRun(final Map<String, Integer> registers)
        {
            return switch (_condition)
            {
                case "==" -> registers.getOrDefault(_op1, 0) == Integer.parseInt(_op2);
                case "!=" -> registers.getOrDefault(_op1, 0) != Integer.parseInt(_op2);
                case ">=" -> registers.getOrDefault(_op1, 0) >= Integer.parseInt(_op2);
                case "<=" -> registers.getOrDefault(_op1, 0) <= Integer.parseInt(_op2);
                case "<" -> registers.getOrDefault(_op1, 0) < Integer.parseInt(_op2);
                case ">" -> registers.getOrDefault(_op1, 0) > Integer.parseInt(_op2);
                default -> false;
            };
        }
    }
}
