package services;

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

}
