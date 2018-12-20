package model.dao;

import model.entities.Layer;
import model.entities.Mean;
import model.entities.Slot;

import java.util.List;

public interface ILayerDAO {

    Layer layerById(long id);
    void saveOrUpdate(Layer layer);
    List<Layer> getLyersOfMean(Mean mean);
    Layer getNextLayerToSchedule(Mean mean);
    Layer getLayerToScheduleForSlot(Slot slot);
    Layer getLayerAtPriority(Mean mean, int priority);
    Layer create(Mean mean);
    void delete(Layer layer);
}
