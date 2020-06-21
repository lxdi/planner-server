package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Slot;

import java.util.List;

public interface ILayerDAO {

    Layer layerById(long id);
    void saveOrUpdate(Layer layer);
    List<Layer> getLyersOfMean(Mean mean);
    List<Layer> getLyersOfMeans(List<Mean> means);
    Layer getNextLayerToSchedule(Mean mean);
    Layer getLayerAtPriority(Mean mean, int priority);
    Layer create(Mean mean);
    void delete(Layer layer);
    long taskCountInLayer(Layer layer);
}
