package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Alexander on 26.04.2018.
 */

@Transactional
public interface ITasksDAO extends JpaRepository<Task, String> {

    Task findByTitle(String title);
    List<Task> findByLayer(Layer layer);

    @Query("from Task where layer.mean = :mean")
    List<Task> findByMean(@Param("mean") Mean mean);

}
