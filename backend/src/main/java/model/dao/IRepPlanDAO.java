package model.dao;

import model.entities.RepetitionPlan;

import java.util.List;

public interface IRepPlanDAO {

    void save(RepetitionPlan repetitionPlan);
    RepetitionPlan getById(long id);
    RepetitionPlan getByTitle(String title);
    List<RepetitionPlan> getAll();
}
