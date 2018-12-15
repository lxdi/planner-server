package model.dao;

import model.entities.HQuarter;
import model.entities.Week;

import java.util.List;

public interface IHQuarterDAO {

    void saveOrUpdate(HQuarter HQuarter);
    HQuarter getById(long id);
    List<HQuarter> getAllHQuartals();
    List<HQuarter> getDefaultHquarters();
    HQuarter getDefault();
    HQuarter getHquarterWithStartingWeek(Week week);

}
