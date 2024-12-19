package de.hendriklipka.aoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie
{
    TrieNode root = new TrieNode();

    public void insert(String s)
    {
        root.insert(s, 0);
    }

    public int getLongestPrefix(String s)
    {
        return root.getLongestPrefix(s, 0);
    }

    public List<String> getAllPrefixes(String s)
    {
        return root.getAllPrefixes(s, 0);
    }

    public boolean contains(String s)
    {
        return root.contains(s, 0);
    }

    private static class TrieNode
    {
        boolean isFinal = false;
        Map<Character, TrieNode> children = new HashMap<>();

        public void insert(String s, int offset)
        {
            if (offset == s.length())
            {
                isFinal = true;
                return;
            }
            char c = s.charAt(offset);
            TrieNode child = children.computeIfAbsent(c, x -> new TrieNode());
            child.insert(s, offset + 1);
        }

        public int getLongestPrefix(String s, int offset)
        {
            // the string matches the current trie path, so we are done
            if (offset == s.length())
            {
                return isFinal ? 0 : -1;
            }
            TrieNode child = children.get(s.charAt(offset));
            // we have no children, so we only match when this is a final node
            if (null == child)
            {
                return isFinal? 0 : -1;
            }
            int len = child.getLongestPrefix(s, offset + 1);
            if (-1 == len)
                return isFinal? 0 : -1;
            return len + 1;
        }

        public List<String> getAllPrefixes(String s, int offset)
        {
            // the string matches the current trie path, so we are done
            if (offset == s.length())
            {
                return isFinal ? List.of(s) : null;
            }
            TrieNode child = children.get(s.charAt(offset));
            // we have no children, so we only match when this is a final node
            if (null == child)
            {
                // when this is a final node, the current prefix is a match, otherwise we have no matches
                return isFinal ? List.of(s.substring(0, offset)) : null;
            }
            List<String> subMatches = child.getAllPrefixes(s, offset + 1);
            // when no children match anything, we might return the current prefix
            if (subMatches == null)
                return isFinal ? List.of(s.substring(0, offset)) : null;
            List<String> result = new ArrayList<>(subMatches);
            if (isFinal)
                result.add(s.substring(0, offset));
            return result;
        }

        public boolean contains(final String s, final int offset)
        {
            if (offset==s.length())
            {
                return isFinal;
            }
            TrieNode child = children.get(s.charAt(offset));
            // we have no children, so we only match when this is a final node
            if (null == child)
            {
                return false;
            }
            return child.contains(s, offset + 1);
        }
    }
}
