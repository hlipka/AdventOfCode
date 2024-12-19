package de.hendriklipka.aoc;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class _Trie
{
    Trie root = new Trie();

    @Test
    public void testSingleInsert()
    {
        root.insert("abc");
        assertThat(root.getLongestPrefix("abc"), is(3));
        assertThat(root.getLongestPrefix("ab"), is(-1));
        assertThat(root.getLongestPrefix("a"), is(-1));
    }

    @Test
    public void testMultipleInserts1()
    {
        root.insert("abc");
        root.insert("ab");
        root.insert("abcdef");
        assertThat(root.getLongestPrefix("abcdef"), is(6));
        assertThat(root.getLongestPrefix("abc"), is(3));
        assertThat(root.getLongestPrefix("ab"), is(2));
        assertThat(root.getLongestPrefix("a"), is(-1));
    }

    @Test
    public void testMultipleInserts2()
    {
        root.insert("abc");
        root.insert("ab");
        root.insert("abcdef");
        root.insert("abcxyz");
        root.insert("axyz");
        root.insert("xyz");
        root.insert("x");
        assertThat(root.getLongestPrefix("abcdefghi"), is(6));
        assertThat(root.getLongestPrefix("abcdef"), is(6));
        assertThat(root.getLongestPrefix("abcdefg"), is(6));
        assertThat(root.getLongestPrefix("abc"), is(3));
        assertThat(root.getLongestPrefix("ab"), is(2));
        assertThat(root.getLongestPrefix("a"), is(-1));
        assertThat(root.getLongestPrefix("yz"), is(-1));
        assertThat(root.getLongestPrefix("x"), is(1));
        assertThat(root.getLongestPrefix("xx"), is(1));
        assertThat(root.getLongestPrefix("xxxxx"), is(1));
        assertThat(root.getLongestPrefix("xxyz"), is(1));
        assertThat(root.getLongestPrefix("axy"), is(-1));
        assertThat(root.getLongestPrefix("xyz"), is(3));
        assertThat(root.getLongestPrefix("adskjghf"), is(-1));
        assertThat(root.getLongestPrefix("abcd"), is(3));
        assertThat(root.getLongestPrefix("iuz"), is(-1));
    }

    @Test
    public void testPrefixList()
    {
        root.insert("abc");
        root.insert("ab");
        root.insert("a");
        root.insert("abb");
        root.insert("aba");
        root.insert("xx");
        root.insert("xxx");


        assertThat(root.getAllPrefixes("xxxx").size(), is(2));
        assertThat(root.getAllPrefixes("xxxx").get(0), is("xxx"));
        assertThat(root.getAllPrefixes("xxxx").get(1), is("xx"));
        assertThat(root.getAllPrefixes("abbb").size(), is(3));
    }

    @Test
    public void testContains()
    {
        root.insert("abc");
        root.insert("ab");
        root.insert("abcdef");
        root.insert("abcxyz");
        root.insert("axyz");
        root.insert("xyz");
        root.insert("x");

        assertTrue(root.contains("abc"));
        assertTrue(root.contains("ab"));
        assertTrue(root.contains("abcdef"));
        assertTrue(root.contains("xyz"));
        assertFalse(root.contains("a"));
        assertFalse(root.contains("abcd"));
        assertFalse(root.contains("xy"));
        assertFalse(root.contains("cx"));

    }
}
