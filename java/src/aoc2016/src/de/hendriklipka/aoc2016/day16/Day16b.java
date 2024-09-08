package de.hendriklipka.aoc2016.day16;

/**
 * User: hli
 * Date: 9/8/24
 * Time: 8:35 PM
 */
public class Day16b
{
    public static void main(String[] args)
    {
        DiskFiller df = new DiskFiller("01111001100111011", 35651584);
        df.fillDisk();
        CheckSum cs = new CheckSum(df.getData());
        System.out.println(cs.getCheckSum());
    }

    private static class DiskFiller
    {
        private String _data;
        private final int _len;
        private final String _startState;

        public DiskFiller(String state, int len)
        {
            _startState = state;
            _len = len;
        }

        private void fillDisk()
        {
            StringBuilder current = new StringBuilder(_startState);
            while (current.length() < _len)
            {
                int l = current.length();
                current.append("0");
                for (int i=0;i<l;i++)
                {
                    char c = current.charAt(l-i-1);
                    current.append(c=='0'?'1':'0');
                }
            }
            _data = current.substring(0,_len);
        }

        public String getData()
        {
            return _data;
        }
    }

    private static class CheckSum
    {
        private String _checkSum;

        public CheckSum(String data)
        {
            _checkSum = data;
        }

        public String getCheckSum()
        {
            while ( 0== (_checkSum.length()%2))
            {
                StringBuilder data =new StringBuilder();
                for (int i=0;i<_checkSum.length()/2;i++)
                {
                    if (_checkSum.charAt(2*i)==_checkSum.charAt(2*i+1))
                    {
                        data.append('1');
                    }
                    else
                    {
                        data.append('0');
                    }
                }
                _checkSum = data.toString();
            }
            return _checkSum;
        }
    }
}
