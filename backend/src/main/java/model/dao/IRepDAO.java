package model.dao;

import model.entities.Repetition;

import java.util.List;

public interface IRepDAO {

    void save(Repetition repetition);
    List<Repetition> getRepsbySpacedRepId(long srId);

}
