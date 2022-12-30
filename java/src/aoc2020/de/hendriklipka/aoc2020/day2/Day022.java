package de.hendriklipka.aoc2020.day2;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day022
{
    static Pattern PWD=Pattern.compile("([0-9]+)-([0-9]+) ([a-z]): ([a-z]+)");
    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("data/day2.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            long count=lines.stream().filter(Day022::pwdOK).count();
            System.out.println(count);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static boolean pwdOK(String pwd)
    {
        Matcher m=PWD.matcher(pwd);
        m.matches();
        String strFirstPos=m.group(1);
        String strSecondPos=m.group(2);
        String strChar=m.group(3);
        String strPwd=m.group(4);
        int firstPos=Integer.parseInt(strFirstPos);
        int secondPos=Integer.parseInt(strSecondPos);
        char matchChar=strChar.charAt(0);


        boolean correct = (strPwd.charAt(firstPos-1)==matchChar) ^ (strPwd.charAt(secondPos-1)==matchChar);
        return correct;
    }
}
