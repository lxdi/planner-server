package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.LayerOrder;
import com.sogoodlabs.planner.model.entities.Mean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ILayerOrderDAO extends JpaRepository<LayerOrder, String> {

    Layer findByLayer(Layer layer);

    @Query(value = "SELECT MAX(layerorder) FROM layerorder", nativeQuery = true)
    int getMaxOrder();

}
