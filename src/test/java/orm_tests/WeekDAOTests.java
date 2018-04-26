package orm_tests;

import model.entities.Week;
import org.junit.Test;
import orm_tests.conf.ATestsWithTargetsMeansWeeks;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 23.04.2018.
 */
public class WeekDAOTests extends ATestsWithTargetsMeansWeeks {

    @Test
    public void weekDAOgetWeeeksOfYearTest(){
        List<Week> weeksIn2018 = weekDAO.getWeeksOfYear(2018);
        assertTrue(weeksIn2018.get(0).getYear()==2018);
        assertTrue(weeksIn2018.get(20).getYear()==2018);
        assertTrue(weeksIn2018.get(35).getYear()==2018);
        assertTrue(weeksIn2018.size()==53);
        assertTrue(weeksIn2018.get(weeksIn2018.size()-1).getYear()==2018);
    }

    @Test
    public void weekDAOgetWeeksMapTest(){
        Map<Integer, List<Week>> weekshmap = weekDAO.getWeeksMap();
        assertTrue(weekshmap.get(2018).size()==53);
        assertTrue(weekshmap.get(2018).get(0).getYear()==2018);
        assertTrue(weekshmap.get(2017).get(0).getYear()==2017);
        assertTrue(weekshmap.get(2019).get(0).getYear()==2019);
    }

    @Test
    public void weekByStartDateTest(){
        Week week = weekDAO.weekByStartDate(9, 7, 2018);
        assertTrue(week!=null);
        assertTrue(week.getYear()==2018);
        assertTrue(week.getStartDay()==9);
        assertTrue(week.getStartMonth()==7);
    }

}
