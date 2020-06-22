package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Week;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.DateUtils;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;
import com.sogoodlabs.planner.services.QuarterGenerator;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.sogoodlabs.planner.services.DateUtils.toDate;

/**
 * Created by Alexander on 23.04.2018.
 */

@Transactional
public class WeekDAOTests extends AbstractTestsWithTargets {

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO hquarterDao;

    @Autowired
    IDayDao dayDao;

    @Test
    public void weekByYearAndNumberTest(){
        Date date = toDate("2017-03-17");
        assertTrue(date!=null);

        Day day = new Day(date);
        dayDao.save(day);
        Week week = new Week(day, null, 1);
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
            assertTrue(week.getStartDay().getDate().after(weekPrev.getStartDay().getDate()));
        }
    }

    @Test
    public void weekOfDateTest(){
        quarterGenerator.generateYear(2019);
        checkDayInWeek("2019-03-26", "2019-03-25", "2019-03-31");
        checkDayInWeek("2019-03-25", "2019-03-25", "2019-03-31");
        checkDayInWeek("2019-03-31", "2019-03-25", "2019-03-31");
        checkDayInWeek("2019-05-02", "2019-04-29", "2019-05-05");
    }

    @Test
    public void lastWeekInYearTest(){
        quarterGenerator.generateYear(2018);
        assertEquals(DateUtils.fromDate(weekDAO.lastWeekInYear(2018).getStartDay().getDate()),"2018-12-31");

        quarterGenerator.generateYear(2019);
        assertEquals(DateUtils.fromDate(weekDAO.lastWeekInYear(2019).getStartDay().getDate()),"2019-12-30");

    }

    @Test
    public void getLastInYearTest(){
        quarterGenerator.generateYear(2018);
        quarterGenerator.generateYear(2019);
        HQuarter lastHquarter = hquarterDao.getLastInYear(2018);
        assertTrue(hquarterDao.getHQuartersInYear(2018).get(11).getId()==lastHquarter.getId());
    }

    private void checkDayInWeek(String dateString, String startDayExpected, String endDayExpected){
        Date date = DateUtils.toDate(dateString);
        Week week = weekDAO.weekOfDate(date);
        assertTrue(DateUtils.fromDate(week.getStartDay().getDate()).equals(startDayExpected));
        assertTrue(DateUtils.fromDate(week.getEndDay().getDate()).equals(endDayExpected));
    }

}