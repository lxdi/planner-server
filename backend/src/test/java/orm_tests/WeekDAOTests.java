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

    //TODO restore
//    @Test
//    public void weekDAOgetWeeeksOfYearTest(){
//        List<Week> weeksIn2018 = weekDAO.getWeeksOfYear(2018);
//        assertTrue(weeksIn2018.get(0).getYear()==2018);
//        assertTrue(weeksIn2018.get(20).getYear()==2018);
//        assertTrue(weeksIn2018.get(35).getYear()==2018);
//        assertTrue(weeksIn2018.size()==53);
//        assertTrue(weeksIn2018.get(weeksIn2018.size()-1).getYear()==2018);
//    }
//
//    @Test
//    public void weekDAOgetWeeksMapTest(){
//        Map<Integer, List<Week>> weekshmap = weekDAO.getWeeksMap();
//        assertTrue(weekshmap.get(2018).size()==53);
//        assertTrue(weekshmap.get(2018).get(0).getYear()==2018);
//        assertTrue(weekshmap.get(2017).get(0).getYear()==2017);
//        assertTrue(weekshmap.get(2019).get(0).getYear()==2019);
//    }
//
//    @Test
//    public void weekByStartDateTest(){
//        Week week = weekDAO.weekByStartDate(9, 7, 2018);
//        assertTrue(week!=null);
//        assertTrue(week.getYear()==2018);
//        assertTrue(week.getStartDay()==9);
//        assertTrue(week.getStartMonth()==7);
//    }

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
