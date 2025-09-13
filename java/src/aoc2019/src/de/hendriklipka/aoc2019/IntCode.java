package de.hendriklipka.aoc2019;

import java.util.List;

public class IntCode
{
    private final List<Integer> _code;

    public IntCode(List<Integer> code)
    {
        _code = code;
    }

    public void execute()
    {
        int pc=0;
        while (true)
        {
            int opCode = get(pc);
            switch (opCode)
            {
                case 1:
                    _code.set(get(pc + 3), get(get(pc + 1)) + get(get(pc + 2)));
                    pc+=4;
                    break;
                case 2:
                    _code.set(get(pc + 3), get(get(pc + 1)) * get(get(pc + 2)));
                    pc+=4;
                    break;
                case 99:
                    return;
            }
        }
    }

    public int get(int pos)
    {
        return _code.get(pos);
    }
}
