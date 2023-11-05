package de.hendriklipka.aoc2016.day5;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.NoSuchAlgorithmException;

/**
 * User: hli
 * Date: 05.11.23
 * Time: 20:19
 */
public class Day05b
{
    public static void main(String[] args) throws NoSuchAlgorithmException
    {
        char[] pwd = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        int charsFound=0;
        String doorId="cxdnnyjw";
        int index=0;
        while (charsFound<8)
        {
            String test=doorId+index;
            String md5Hex = DigestUtils.md5Hex(test).toLowerCase();
            if (md5Hex.startsWith("00000"))
            {
                final char posChar = md5Hex.charAt(5);
                final char pwdChar = md5Hex.charAt(6);
                System.out.println("found something at "+index+": pos) "+ posChar +", char="+ pwdChar);
                if (posChar>='0' && posChar<='7' && pwd[posChar-'0']==' ')
                {
                    System.out.println("found new character");
                    pwd[posChar - '0']=pwdChar;
                    charsFound++;
                }
            }
            index++;
        }
        System.out.println(pwd);
    }
}
