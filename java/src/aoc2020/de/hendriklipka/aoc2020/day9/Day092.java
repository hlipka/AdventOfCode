package de.hendriklipka.aoc2020.day9;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Day092
{
    static long NUM=1309761972;

    public static void main(String[] args)
    {
        try
        {
            List<Long> nums = FileUtils.readLines(new File("../data/2020/day9.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
            for (int i = 0; i<nums.size(); i++)
            {
                long result=findSum(nums, i, NUM);
                if (-1!=result)
                {
                    System.out.println(result);
                    break;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static long findSum(List<Long> nums, int start, long num)
    {
        long sum=nums.get(start);
        int pos=start+1;
        long min=sum;
        long max=sum;
        while (sum<num)
        {
            Long current = nums.get(pos++);
            sum+= current;
            min=Math.min(min,current);
            max=Math.max(max,current);
        }
        if (sum==num)
        {
            return min+max;
        }
        return -1;
    }
}
