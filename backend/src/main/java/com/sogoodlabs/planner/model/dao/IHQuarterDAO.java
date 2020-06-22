package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Week;

import java.sql.Date;
import java.util.List;

public interface IHQuarterDAO {

    void saveOrUpdate(HQuarter HQuarter);
    HQuarter getById(long id);
    List<HQuarter> getAllHQuartals();
    List<HQuarter> getWorkingHquarters();
    List<HQuarter> getHQuartersInYear(int year);
    HQuarter getDefault();
    HQuarter getHquarterWithStartingWeek(Week week);
    List<HQuarter> getPrev(long currentid, int count);
    List<HQuarter> getNext(long currentid, int count);
    HQuarter getByDate(Date date);
    HQuarter getLastInYear(int year);

}