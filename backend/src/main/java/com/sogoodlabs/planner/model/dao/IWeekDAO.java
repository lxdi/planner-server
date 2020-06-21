package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Week;

import java.sql.Date;
import java.util.List;

/**
 * Created by Alexander on 22.04.2018.
 */
public interface IWeekDAO {

    Week getById(long id);
    void saveOrUpdate(Week week);
    List<Week> allWeeks();
    Week weekByStartDay(Day day);
    Week weekByYearAndNumber(int year, int number);
    List<Week> weeksOfHquarter(HQuarter hQuarter);
    Week weekOfDate(Date date);
    Week lastWeekInYear(int year);

}
