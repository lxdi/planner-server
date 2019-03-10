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

}
