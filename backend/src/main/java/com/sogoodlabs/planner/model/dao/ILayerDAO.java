package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ILayerDAO extends JpaRepository<Layer, String> {

    List<Layer> findByMean(Mean mean);

    @Query("FROM Layer WHERE priority > 0")
    List<Layer> findWithPriority();

}
