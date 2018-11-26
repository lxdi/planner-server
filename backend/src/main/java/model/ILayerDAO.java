package model;

import model.entities.Layer;
import model.entities.Mean;

import java.util.List;

public interface ILayerDAO {

    void saveOrUpdate(Layer layer);
    List<Layer> getLyersOfMean(Mean mean);
    Layer create(Mean mean);
    void delete(Layer layer);
}
