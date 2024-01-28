package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Realm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ILayerDAO extends JpaRepository<Layer, String> {

    List<Layer> findByMean(Mean mean);

    @Query("FROM Layer WHERE priority > 0")
    List<Layer> findWithPriority();

    @Query("FROM Layer WHERE mean.realm = :realm ORDER BY priority DESC")
    List<Layer> findByRealmOrderByPriority(@Param("realm") Realm realm, Pageable limit);

}
