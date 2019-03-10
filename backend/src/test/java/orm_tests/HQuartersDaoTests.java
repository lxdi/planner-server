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

    @Ignore
    @Test
    public void allHquartersTest(){
        List<HQuarter> HQuarters = hquarterDAO.getAllHQuartals();
        assertTrue(HQuarters.size()==16);
        int checks = 4;
        for (HQuarter HQuarter : HQuarters) {
            String startWeekDateStr = fromDate(HQuarter.getStartWeek().getStartDay());
            if (startWeekDateStr.contains("2018-01-01")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-03-26")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-06-18")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-09-10")) {
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

//    @Test
//    public void deleteMeanWithQuarter(){
//        int quartersAtTheBeginning = hquartalDAO.getAllHQuartals().size();
//
//        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
//        meansDao.assignQuarter(thirdHQuarter, meanParent, 1);
//
//        meansDao.deleteMean(meansDao.meanByTitle(parentMeanTitle).getId());
//
//        assertTrue(hquartalDAO.getAllHQuartals().size()==quartersAtTheBeginning);
//    }
//
//    @Test(expected = RuntimeException.class)
//    public void assigningMeanOnOccupiedPlace(){
//        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
//        Mean meanChild = meansDao.meanByTitle(childMeanTitle);
//        Mean meanChild2 = meansDao.meanByTitle(child2MeanTitle);
//
//        meansDao.assignQuarter(hquartalDAO.getById(this.secondHQuarter.getId()), meanParent, 1);
//        meansDao.assignQuarter(hquartalDAO.getById(this.secondHQuarter.getId()), meanChild, 2);
//        meansDao.assignQuarter(hquartalDAO.getById(this.secondHQuarter.getId()), meanChild2, 1);
//    }

}
