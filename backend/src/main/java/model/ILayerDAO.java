package model;

import model.entities.Layer;
import model.entities.Mean;

import java.util.List;

public interface ILayerDAO {

    List<Layer> getLyersOfMean(Mean mean);
    void saveOrUpdate(Layer layer);

}
