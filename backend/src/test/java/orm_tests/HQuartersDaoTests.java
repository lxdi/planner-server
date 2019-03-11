package orm_tests;

import model.dao.HQuarterDao;
import model.dao.IWeekDAO;
import model.entities.HQuarter;
import model.entities.Mean;
import model.entities.Week;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.ATestsWithTargetsMeansQuartals;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;
import orm_tests.conf.AbstractTestsWithTargets;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static services.DateUtils.fromDate;
import static services.DateUtils.toDate;


public class HQuartersDaoTests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    IWeekDAO weekDAO;

    @Before
    public void init(){
        super.init();
    }

    @Test
    public void allHquartersTest(){
        List<HQuarter> HQuarters = hquarterDAO.getAllHQuartals();
        assertTrue(HQuarters.size()==24);
        int checks = 12;
        for (HQuarter HQuarter : HQuarters) {
            String startWeekDateStr = fromDate(HQuarter.getStartWeek().getStartDay());
            if (startWeekDateStr.contains("2018-01-01")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-01-29")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-02-26")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-03-26")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-04-23")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-05-21")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-06-18")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-07-16")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-08-13")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-09-10")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-10-08")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-11-05")) {
                checks--;
            }
        }
        assertTrue(checks==0);
    }

    @Test
    public void getHquarterWithStartingWeekTest(){
        //Week week = weekDAO.weekByStartDate(toDate("2018-01-01"));
        Week week = weekDAO.weekByYearAndNumber(2018, 1);
        HQuarter hQuarter = hquarterDAO.getHquarterWithStartingWeek(week);

        assertTrue(hQuarter.getStartWeek().getId() == week.getId());
    }

    @Test
    public void getHQuartersInYearTest(){
        List<HQuarter> hQuartersIn2018 = hquarterDAO.getHQuartersInYear(2018);
        assertTrue(hQuartersIn2018.size()==12);
        for(int i =1; i<hQuartersIn2018.size(); i++){
            HQuarter prev = hQuartersIn2018.get(i-1);
            HQuarter current = hQuartersIn2018.get(i);
            assertTrue(current.getStartWeek().getStartDay().after(prev.getStartWeek().getStartDay()));
        }

        assertTrue(hquarterDAO.getHQuartersInYear(2019).size()==12);
    }

}
