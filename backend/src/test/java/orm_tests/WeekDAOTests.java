package orm_tests;

import model.dao.IWeekDAO;
import model.entities.Week;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.ATestsWithTargetsMeansWeeks;
import orm_tests.conf.AbstractTestsWithTargets;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static services.DateUtils.toDate;

/**
 * Created by Alexander on 23.04.2018.
 */
public class WeekDAOTests extends AbstractTestsWithTargets {

    @Autowired
    IWeekDAO weekDAO;

    @Test
    public void gettingSameWeekByDateTest(){
        Date date = toDate("2017-03-17");
        assertTrue(date!=null);

        Week week = new Week(date, null, 1);
        weekDAO.saveOrUpdate(week);

        Week sameWeek = weekDAO.weekByStartDate(toDate("2017-03-17"));

        assertTrue(week.getId()==sameWeek.getId());
    }

    @Test
    public void weekByYearAndNumberTest(){
        Date date = toDate("2017-03-17");
        assertTrue(date!=null);

        Week week = new Week(date, null, 1);
        weekDAO.saveOrUpdate(week);

        Week sameWeek = weekDAO.weekByYearAndNumber(2017, 1);

        assertTrue(week.getId()==sameWeek.getId());
    }

}
