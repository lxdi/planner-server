package model;

import model.entities.Mean;
import model.entities.Quartal;

public interface IQuartalDAO {

    void saveOrUpdate(Quartal quartal);
    Quartal getById(long id);
    Quartal getWithMean(Mean mean);
    void assignMean(Quartal quartal, Mean mean);

}
