package model.dao;

import model.entities.HQuarter;

import java.util.List;

public interface IHQuarterDAO {

    void saveOrUpdate(HQuarter HQuarter);
    HQuarter getById(long id);
    List<HQuarter> getAllHQuartals();
    List<HQuarter> getDefaultHquarters();
    HQuarter getByStartDate(int year, int month, int day);
    HQuarter getDefault();

}
