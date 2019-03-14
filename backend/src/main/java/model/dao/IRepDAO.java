package model.dao;

import model.entities.Repetition;

import java.sql.Date;
import java.util.List;

public interface IRepDAO {

    void save(Repetition repetition);
    List<Repetition> getRepsbySpacedRepId(long srId);
    List<Repetition> getWithPlanDateInRange(Date from, Date to);

}
