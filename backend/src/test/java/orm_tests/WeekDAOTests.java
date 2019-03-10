package orm_tests;

import model.dao.IHQuarterDAO;
import model.dao.IWeekDAO;
import model.entities.HQuarter;
import model.entities.Week;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.ATestsWithTargetsMeansWeeks;
import orm_tests.conf.AbstractTestsWithTargets;
import services.QuarterGenerator;

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

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO hquarterDao;

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

    @Test
    public void gettingWeeksInHQuarterTest(){
        quarterGenerator.generateYear(2018);

        Week startWeek = weekDAO.weekByYearAndNumber(2018, 1);
        HQuarter hQuarter = hquarterDao.getHquarterWithStartingWeek(startWeek);

        List<Week> weeks = weekDAO.weeksOfHquarter(hQuarter);

        assertTrue(weeks.size()==4);
        assertTrue(weeks.get(0).getId() == startWeek.getId());
        for(int i = 1; i<weeks.size();  i++){
            Week weekPrev = weeks.get(i-1);
            Week week= weeks.get(i);
            assertTrue(week.getStartDay().after(weekPrev.getStartDay()));
        }
    }

}
