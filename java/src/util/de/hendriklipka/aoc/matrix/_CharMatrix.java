package de.hendriklipka.aoc.matrix;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class _CharMatrix
{
    @Test
    public void testRotate2by2()
    {
        CharMatrix m=CharMatrix.fromStringList(List.of("12", "34"), ' ');
        CharMatrix rot=m.rotate();
        assertThat(rot.at(0,0), is('3'));
        assertThat(rot.at(0,1), is('1'));
        assertThat(rot.at(1,0), is('4'));
        assertThat(rot.at(1,1), is('2'));
    }

    @Test
    public void testRotate3by3()
    {
        CharMatrix m=CharMatrix.fromStringList(List.of("123", "456", "789"), ' ');
        CharMatrix rot=m.rotate();
        assertThat(rot.at(0,0), is('7'));
        assertThat(rot.at(0,1), is('4'));
        assertThat(rot.at(0,2), is('1'));
        assertThat(rot.at(1,0), is('8'));
        assertThat(rot.at(1,1), is('5'));
        assertThat(rot.at(1,2), is('2'));
        assertThat(rot.at(2,0), is('9'));
        assertThat(rot.at(2,1), is('6'));
        assertThat(rot.at(2,2), is('3'));
    }

}
