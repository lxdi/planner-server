package model;

import model.entities.Mean;

import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */
public interface MeansDAO {

    List<Mean> getAllMeans();
    Mean meanById();

}
