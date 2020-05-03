package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.SpacedRepetitions;

public interface ISpacedRepDAO {

    void save(SpacedRepetitions spacedRepetitions);
    SpacedRepetitions getSRforTaskMapper(long tmId);
    SpacedRepetitions getSRforTask(long taskid);

}
