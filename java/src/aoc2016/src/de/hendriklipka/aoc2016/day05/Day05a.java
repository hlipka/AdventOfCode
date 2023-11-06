package de.hendriklipka.aoc2016.day05;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.NoSuchAlgorithmException;

/**
 * User: hli
 * Date: 05.11.23
 * Time: 20:19
 */
public class Day05a
{
    public static void main(String[] args) throws NoSuchAlgorithmException
    {
        StringBuilder pwd=new StringBuilder();
        String doorId="cxdnnyjw";
        int index=0;
        while (pwd.length()<8)
        {
            String test=doorId+index;
            String md5Hex = DigestUtils.md5Hex(test).toLowerCase();
            if (md5Hex.startsWith("00000"))
            {
                System.out.println("found next at "+index+" as "+ md5Hex.charAt(5));
                pwd.append(md5Hex.charAt(5));
            }
            index++;
        }
        System.out.println(pwd);
    }
}
