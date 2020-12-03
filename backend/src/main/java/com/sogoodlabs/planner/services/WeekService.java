package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WeekService {

    @Autowired
    private IDayDao dayDao;

    public void fill(Week week){
        week.setDays(new ArrayList<>());
        dayDao.findByWeek(week).forEach(week.getDays()::add);
    }

}
