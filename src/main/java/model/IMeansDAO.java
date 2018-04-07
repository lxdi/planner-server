package model;

import model.entities.Mean;

import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */
public interface IMeansDAO {

    List<Mean> getAllMeans();
    Mean meanById(long id);
    void saveOrUpdate(Mean mean);

}
