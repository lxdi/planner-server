package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Week;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.StringUtils;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;
import com.sogoodlabs.planner.services.DateUtils;
import com.sogoodlabs.planner.services.QuarterGenerator;
import com.sogoodlabs.planner.test_configs.TestCreatorsAnotherSession;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class HquartersDelegateTests extends AbstractTestsWithTargets {

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO hQuarterDAO;

    @Autowired
    HquartersDelegate hquartersDelegate;

    @Before
    public void init(){
        super.init();
    }

    @Test
    public void getPrevTest(){
        quarterGenerator.generateYear(2018);
        List<HQuarter> hQuarters2018 = hQuarterDAO.getHQuartersInYear(2018);
        HQuarter firstHquarter = hQuarters2018.get(0);
        assertTrue(DateUtils.fromDate(firstHquarter.getStartWeek().getStartDay().getDate()).equals("2018-01-01"));
        assertTrue(hQuarterDAO.getHQuartersInYear(2017).size()==0);

        List<Map<String, Object>> hQuartersLazy2017 = hquartersDelegate.getPrev(firstHquarter.getId(), 4);

        assertTrue(hQuartersLazy2017.size()==4);
        assertTrue(StringUtils.getValue(hQuartersLazy2017, "get(0).get('startWeek').get('startDay')").equals("2017-08-14"));

    }

    @Test
    public void getNextTest(){
        quarterGenerator.generateYear(2018);
        List<HQuarter> hQuarters2018 = hQuarterDAO.getHQuartersInYear(2018);
        HQuarter lastHquarter = hQuarters2018.get(11);
        assertTrue(DateUtils.fromDate(lastHquarter.getStartWeek().getStartDay().getDate()).equals("2018-11-05"));
        assertTrue(hQuarterDAO.getHQuartersInYear(2019).size()==0);

        List<Map<String, Object>> hQuartersLazy2019 = hquartersDelegate.getNext(lastHquarter.getId(), 4);

        assertTrue(hQuartersLazy2019.size()==4);
        assertTrue(StringUtils.getValue(hQuartersLazy2019, "get(0).get('startWeek').get('startDay')").equals("2019-01-07"));

    }

    @Test
    public void getCurrentHquartersWithoutSlotsTest(){
        hquartersDelegate.getCurrentHquarters();
    }

    @Test
    public void removeFromHquartersListTillDateTest(){
        quarterGenerator.generateYear(2019);
        List<HQuarter> hQuarters2019 = hQuarterDAO.getHQuartersInYear(2019);

        hquartersDelegate.removeFromHquartersListTillDate(hQuarters2019, DateUtils.toDate("2019-04-02"));

        assertTrue(hQuarters2019.size()==10);
        assertTrue(DateUtils.fromDate(hQuarters2019.get(0).getStartWeek().getStartDay().getDate()).equals("2019-03-04"));
        assertTrue(DateUtils.fromDate(hQuarters2019.get(1).getStartWeek().getStartDay().getDate()).equals("2019-04-01"));
        assertTrue(DateUtils.fromDate(hQuarters2019.get(2).getStartWeek().getStartDay().getDate()).equals("2019-04-29"));
        assertTrue(DateUtils.fromDate(hQuarters2019.get(3).getStartWeek().getStartDay().getDate()).equals("2019-05-27"));
        assertTrue(DateUtils.fromDate(hQuarters2019.get(4).getStartWeek().getStartDay().getDate()).equals("2019-06-24"));
        assertTrue(DateUtils.fromDate(hQuarters2019.get(5).getStartWeek().getStartDay().getDate()).equals("2019-07-22"));
        assertTrue(DateUtils.fromDate(hQuarters2019.get(6).getStartWeek().getStartDay().getDate()).equals("2019-08-19"));

    }

    @Test
    public void getCurrentHquartersTest(){
        Date currentDateFake = DateUtils.toDate("2019-04-02");

        List<HQuarter> result = hquartersDelegate.getCurrentHquarters(currentDateFake);

        assertTrue(result.size()==12);
        assertTrue(DateUtils.fromDate(result.get(0).getStartWeek().getStartDay().getDate()).equals("2019-03-04"));
        assertTrue(DateUtils.fromDate(result.get(1).getStartWeek().getStartDay().getDate()).equals("2019-04-01"));
        assertTrue(DateUtils.fromDate(result.get(2).getStartWeek().getStartDay().getDate()).equals("2019-04-29"));
        assertTrue(DateUtils.fromDate(result.get(3).getStartWeek().getStartDay().getDate()).equals("2019-05-27"));
        assertTrue(DateUtils.fromDate(result.get(4).getStartWeek().getStartDay().getDate()).equals("2019-06-24"));
        assertTrue(DateUtils.fromDate(result.get(5).getStartWeek().getStartDay().getDate()).equals("2019-07-22"));
        assertTrue(DateUtils.fromDate(result.get(6).getStartWeek().getStartDay().getDate()).equals("2019-08-19"));
        assertTrue(DateUtils.fromDate(result.get(7).getStartWeek().getStartDay().getDate()).equals("2019-09-16"));
        assertTrue(DateUtils.fromDate(result.get(8).getStartWeek().getStartDay().getDate()).equals("2019-10-14"));
        assertTrue(DateUtils.fromDate(result.get(9).getStartWeek().getStartDay().getDate()).equals("2019-11-11"));
        assertTrue(DateUtils.fromDate(result.get(10).getStartWeek().getStartDay().getDate()).equals("2020-01-06"));
        assertTrue(DateUtils.fromDate(result.get(11).getStartWeek().getStartDay().getDate()).equals("2020-02-03"));
    }




}