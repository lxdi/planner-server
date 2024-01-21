package com.sogoodlabs.planner.util;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.DaysOfWeek;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SortUtilsTest {

    @Test
    public void sortDistantDaysTest(){
        List<Day> result = SortUtils.sortDistantDays(Arrays.asList(createDay(1), createDay(2), createDay(3)));

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("3", result.get(1).getId());

    }

    @Test
    public void sortDistantDaysTest2(){
        List<Day> result = SortUtils.sortDistantDays(Arrays.asList(createDay(1), createDay(2)));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());

    }

    @Test
    public void sortDistantDaysTest3(){
        List<Day> result = SortUtils.sortDistantDays(Arrays.asList(createDay(1)));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());

    }

    @Test
    public void sortDistantDaysTest4(){
        List<Day> result = SortUtils.sortDistantDays(Arrays.asList(createDay(1), createDay(3), createDay(5)));

        assertEquals(3, result.size());

    }

    @Test
    public void sortDistantDaysTest5(){
        List<Day> result = SortUtils.sortDistantDays(Arrays.asList(createDay(1), createDay(2), createDay(3), createDay(4)));

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("3", result.get(1).getId());
    }

    private Day createDay(int id){
        Day day = new Day();
        day.setId("" + id);
        day.setWeekDay(DaysOfWeek.findById(id));
        return day;
    }


}
