package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class    Day21 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day21().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        Scrambler scr= new Scrambler();
        testScrambler(scr);
        data.getLines().forEach(scr::addLine);

        if (isExample)
            return scr.scramble("abcde");
        else
            return scr.scramble("abcdefgh");
    }

    private void testScrambler(final Scrambler scr)
    {
        assertThat(scr.scramble("abcde", "swap position 4 with position 0"), is("ebcda"));
        assertThat(scr.scramble("ebcda", "swap letter d with letter b"), is("edcba"));
        assertThat(scr.scramble("edcba", "reverse positions 0 through 4"), is("abcde"));
        assertThat(scr.scramble("abcde", "rotate left 1 step"), is("bcdea"));
        assertThat(scr.scramble("abcde", "rotate left 2 steps"), is("cdeab"));
        assertThat(scr.scramble("abcde", "rotate right 1 step"), is("eabcd"));
        assertThat(scr.scramble("abcde", "rotate right 6 steps"), is("eabcd"));
        assertThat(scr.scramble("abcde", "rotate right 2 steps"), is("deabc"));
        assertThat(scr.scramble("abcde", "rotate right 7 steps"), is("deabc"));
        assertThat(scr.scramble("abcde", "reverse positions 1 through 3"), is("adcbe"));
        assertThat(scr.scramble("abcde", "reverse positions 1 through 2"), is("acbde"));
        assertThat(scr.scramble("abcde", "reverse positions 1 through 4"), is("aedcb"));

        assertThat(scr.scramble("bcdea", "move position 1 to position 4"), is("bdeac"));
        assertThat(scr.scramble("bdeac", "move position 3 to position 0"), is("abdec"));
        assertThat(scr.scramble("abdec", "rotate based on position of letter b"), is("ecabd"));
        assertThat(scr.scramble("ecabd", "rotate based on position of letter d"), is("decab"));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        if (isExample)
        {
            return "";
        }
        Scrambler scr = new Scrambler();
        testScrambler(scr);
        data.getLines().forEach(scr::addLine);

        String codeChars="abcdefgh";
        final List<Character> s = new ArrayList<>();
        for (char c : codeChars.toCharArray())
        {
            s.add(c);
        }
        // we just brute-force all combinations
        // with my input it takes less than 20000 iterations...
        Collection<List<Character>> perms = CollectionUtils.permutations(s);
        int i=0;
        for (List<Character> p : perms)
        {
            StringBuilder sb = new StringBuilder();
            for (Character ca : p)
            {
                sb.append(ca);
            }
            String code = sb.toString();
            if (scr.scramble(code).equals("fbgdceah"))
            {
                return "unscrambled=" + code;
            }
            i++;
            if (0==(i%10000))
            {
                System.out.println(i);
            }

        }

        return "not found";
    }

    private static class Scrambler
    {
        List<String> lines =new ArrayList<>();

        public void addLine(final String line)
        {
            lines.add(line);
        }

        public String scramble(String code)
        {
            for (String l : lines)
            {
                code = scramble(code, l);
            }
            return code;
        }

        private String scramble(final String code, final String line)
        {
            if (line.startsWith("swap position"))
            {
                final List<String> parts = AocParseUtils.parsePartsFromString(line, "swap position (\\d+) with position (\\d+)");
                int pos1=Integer.parseInt(parts.get(0));
                int pos2=Integer.parseInt(parts.get(1));
                char[] s = code.toCharArray();
                char c=s[pos1];
                s[pos1]=s[pos2];
                s[pos2]=c;
                return new String(s);
            }
            else if (line.startsWith("swap letter"))
            {
                final List<String> parts = AocParseUtils.parsePartsFromString(line, "swap letter (.) with letter (.)");
                char c1=parts.get(0).charAt(0);
                char c2=parts.get(1).charAt(0);
                char[] s = code.toCharArray();
                for (int i=0;i<s.length;i++)
                {
                    if (s[i]==c1)
                    {
                        s[i]=c2;
                    }
                    else if (s[i]==c2)
                    {
                        s[i]=c1;
                    }
                }
                return new String(s);
            }
            else if (line.startsWith("rotate left"))
            {
                final List<String> parts = AocParseUtils.parsePartsFromString(line, "rotate left (\\d+) step.*");
                int steps = Integer.parseInt(parts.get(0));
                char[] s = code.toCharArray();
                for (int i=0;i<steps;i++)
                {
                    rotateLeft(s);
                }
                return new String(s);
            }
            else if (line.startsWith("rotate right"))
            {
                final List<String> parts = AocParseUtils.parsePartsFromString(line, "rotate right (\\d+) step.*");
                int steps = Integer.parseInt(parts.get(0));
                char[] s = code.toCharArray();
                for (int i = 0; i < steps; i++)
                {
                    rotateRight(s);
                }
                return new String(s);
            }
            else if (line.startsWith("rotate based"))
            {
                final List<String> parts = AocParseUtils.parsePartsFromString(line, "rotate based on position of letter (.)");
                char c = parts.get(0).charAt(0);
                int steps=code.indexOf(c);
                if (steps>=4)
                    steps++;
                steps++;
                char[] s = code.toCharArray();
                for (int i = 0; i < steps; i++)
                {
                    rotateRight(s);
                }
                return new String(s);
            }
            else if (line.startsWith("reverse positions"))
            {
                final List<String> parts = AocParseUtils.parsePartsFromString(line, "reverse positions (\\d+) through (\\d+)");
                int pos1 = Integer.parseInt(parts.get(0));
                int pos2 = Integer.parseInt(parts.get(1));
                char[] s = code.toCharArray();
                final var diff = pos2 - pos1;
                final var steps = (0==diff%2)?diff / 2:diff/2+1;
                for (int idx = 0; idx < steps; idx++)
                {
                    char c=s[pos1+idx];
                    s[pos1+idx]=s[pos2-idx];
                    s[pos2-idx]=c;
                }
                return new String(s);
            }
            else if (line.startsWith("move position"))
            {
                final List<String> parts = AocParseUtils.parsePartsFromString(line, "move position (\\d+) to position (\\d+)");
                int pos1 = Integer.parseInt(parts.get(0));
                int pos2 = Integer.parseInt(parts.get(1));
                final List<Character> s = new ArrayList<>();
                for (char c: code.toCharArray())
                {
                    s.add(c);
                }
                char c=s.remove(pos1);
                s.add(pos2, c);
                StringBuilder sb = new StringBuilder();
                for (Character ca: s)
                {
                    sb.append(ca);
                }
                return sb.toString();
            }
            else
            {
                throw new IllegalArgumentException("unknown instruction in "+line);
            }
        }

        private void rotateLeft(final char[] s)
        {
            char c=s[0];
            for (int i=1;i<s.length;i++)
            {
                s[i-1]=s[i];
            }
            s[s.length-1]=c;
        }

        private void rotateRight(final char[] s)
        {
            char c=s[s.length-1];
            for (int i = s.length - 1; i >= 1; i--)
            {
                s[i]=s[i-1];
            }
            s[0]=c;
        }
    }
}
