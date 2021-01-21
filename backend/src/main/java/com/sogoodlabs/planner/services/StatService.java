package com.sogoodlabs.planner.services;


import com.sogoodlabs.planner.model.dao.IExternalTaskDao;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.ITaskMappersDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Optional;

@Service
public class StatService {

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private IExternalTaskDao externalTaskDao;

    public int getWeekProgress(){
        return getProgress(DateUtils.subtractDays(DateUtils.currentDate(), 7));
    }

    public int getMonthProgress(){
        return getProgress(DateUtils.subtractDays(DateUtils.currentDate(), 30));
    }

    public int getHalfYearProgress(){
        return getProgress(DateUtils.subtractDays(DateUtils.currentDate(), 182));
    }

    public int getYearProgress(){
        return getProgress(DateUtils.subtractDays(DateUtils.currentDate(), 365));
    }

    private int getProgress(Date sinceDate){
        int result = repDAO.findTotalFinishedAfter(sinceDate);
        result = result + taskMappersDAO.findTotalFinishedAfter(sinceDate)*2;
        result = result + Optional.ofNullable(externalTaskDao.findHoursFinishedAfter(sinceDate)).orElse(0);
        return result;
    }

}
