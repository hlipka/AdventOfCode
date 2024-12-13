package de.hendriklipka.aoc;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * User: hli
 */
public class _AocParseUtils
{
    @Test
    public void testGetRegexAllGroups()
    {
        assertThat(AocParseUtils.getAllGroupsFromLine("a=12;b=13 c=-14", "\\-?\\d+"), contains("12", "13", "-14"));
        assertThat(AocParseUtils.getAllGroupsFromLine("a=12;b=13 c=-14", "\\d+"), contains("12", "13", "14"));
    }

    @Test
    public void testGetRegexGroups()
    {
        assertThat(AocParseUtils.getGroupsFromLine("a=12;b=13 c=-14", "\\S+=(\\d+);\\S+=\\d+.*=(\\-?\\d+)"), contains("12", "-14"));
    }
}
