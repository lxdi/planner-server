package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.util.DateUtils;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.assertTrue;

public class DateUtilsTests {

    @Test
    public void addDaysTest() {
        Date date = DateUtils.addDays(DateUtils.toDate("2019-01-31"), 2);
        assertTrue(DateUtils.fromDate(date).equals("2019-02-02"));
    }

    @Test
    public void addWeeksTest(){
        assertTrue(DateUtils.fromDate(DateUtils.addWeeks(DateUtils.toDate("2019-03-14"), 1))
                            .equals("2019-03-21"));

        assertTrue(DateUtils.fromDate(DateUtils.addWeeks(DateUtils.toDate("2019-03-14"), 2))
                .equals("2019-03-28"));

        assertTrue(DateUtils.fromDate(DateUtils.addWeeks(DateUtils.toDate("2019-03-14"), 3))
                .equals("2019-04-04"));

        assertTrue(DateUtils.fromDate(DateUtils.addWeeks(DateUtils.toDate("2019-03-21"), -1))
                .equals("2019-03-14"));
    }

    @Test
    public void weeksShiftingTest(){
        Date fromDate = DateUtils.addDays(DateUtils.toDate("2019-03-14"), -3);
        Date toDate = DateUtils.addDays(DateUtils.toDate("2019-03-14"), +3);
        assertTrue(DateUtils.fromDate(fromDate).equals("2019-03-11"));
        assertTrue(DateUtils.fromDate(toDate).equals("2019-03-17"));
        assertTrue(!DateUtils.fromDate(fromDate).equals(DateUtils.fromDate(DateUtils.addWeeks(toDate, -1))));
        assertTrue(DateUtils.fromDate(DateUtils.addWeeks(toDate, -1)).equals("2019-03-10"));
    }

    @Test
    public void differenceInDaysTest(){
        assertTrue(DateUtils.differenceInDays(
                DateUtils.toDate("2019-03-31"), DateUtils.toDate("2019-03-25"))==-6);
        assertTrue(DateUtils.differenceInDays(
                DateUtils.toDate("2019-03-25"), DateUtils.toDate("2019-03-26"))==1);
        assertTrue(DateUtils.differenceInDays(
                DateUtils.toDate("2019-03-25"), DateUtils.toDate("2019-03-25"))==0);
        assertTrue(DateUtils.differenceInDays(
                DateUtils.toDate("2019-03-25"), DateUtils.toDate("2019-03-31"))==6);
    }

    @Test
    public void getDayOfWeekTest(){
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-07"))==6);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-08"))==0);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-09"))==1);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-10"))==2);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-11"))==3);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-12"))==4);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-13"))==5);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-14"))==6);
        assertTrue(DateUtils.dayOfWeek(DateUtils.toDate("2019-04-15"))==0);
    }

}
