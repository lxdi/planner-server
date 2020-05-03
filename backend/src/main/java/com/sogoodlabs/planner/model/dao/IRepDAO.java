package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Repetition;

import java.sql.Date;
import java.util.List;

public interface IRepDAO {

    Repetition findOne(long id);
    void save(Repetition repetition);
    List<Repetition> getRepsbySpacedRepId(long srId);
    List<Repetition> getUnFinishedWithPlanDateInRange(Date from, Date to);

}
