package model.dao;

import model.entities.RepetitionPlan;

public interface IRepPlanDAO {

    void save(RepetitionPlan repetitionPlan);
    RepetitionPlan getById(long id);
}
