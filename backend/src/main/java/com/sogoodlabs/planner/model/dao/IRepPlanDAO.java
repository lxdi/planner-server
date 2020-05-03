package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.RepetitionPlan;

import java.util.List;

public interface IRepPlanDAO {

    void save(RepetitionPlan repetitionPlan);
    RepetitionPlan getById(long id);
    RepetitionPlan getByTitle(String title);
    List<RepetitionPlan> getAll();
}
