package model.dao;

import model.entities.Mean;
import model.entities.Quarter;

import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */
public interface IMeansDAO {

    List<Mean> getAllMeans();
    Mean meanById(long id);
    void saveOrUpdate(Mean mean);
    void deleteMean(long id);
    List<Mean> getChildren(Mean mean);
    Mean meanByTitle(String title);
    void assignQuarter(Quarter quarter, Mean mean, Integer position);
    void validateMean(Mean mean);



}
