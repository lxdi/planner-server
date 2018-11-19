package model;

import model.entities.Mean;
import model.entities.Quarter;

import java.util.List;

public interface IQuarterDAO {

    void saveOrUpdate(Quarter quarter);
    Quarter getById(long id);
    List<Quarter> getAllQuartals();
    Quarter getWithMean(Mean mean);
    void assignMean(Quarter quarter, Mean mean);

}
