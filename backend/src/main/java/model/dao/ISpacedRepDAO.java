package model.dao;

import model.entities.SpacedRepetitions;

public interface ISpacedRepDAO {

    void save(SpacedRepetitions spacedRepetitions);
    SpacedRepetitions getSRforTaskMapper(long tmId);
    SpacedRepetitions getSRforTask(long taskid);

}
