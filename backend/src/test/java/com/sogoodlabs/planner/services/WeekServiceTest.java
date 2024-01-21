package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class WeekServiceTest extends SpringTestConfig {

    @Autowired
    private WeekService weekService;

    @Autowired
    private IDayDao dayDao;

    @Test
    public void getWeeksOnDate_yearBegin(){

        Date date = DateUtils.toDate("2021-01-11");
        List<Week> weekList = weekService.getWeeksOnDate(date);

        assertEquals(WeekService.CURRENT_UP_TO_NEXT_WEEKS + WeekService.CURRENT_UP_TO_PREV_WEEKS + 1, weekList.size());
        assertEquals("2020-12-14", DateUtils.fromDate(dayDao.findByWeek(weekList.get(0)).get(0).getDate()));
        assertEquals("2021-08-09", DateUtils.fromDate(dayDao.findByWeek(weekList.get(weekList.size()-1)).get(0).getDate()));
    }

    @Test
    public void getWeeksOnDate_yearEnd(){

        Date date = DateUtils.toDate("2020-12-21");
        List<Week> weekList = weekService.getWeeksOnDate(date);

        assertEquals(WeekService.CURRENT_UP_TO_NEXT_WEEKS + WeekService.CURRENT_UP_TO_PREV_WEEKS + 1, weekList.size());
    }

}
