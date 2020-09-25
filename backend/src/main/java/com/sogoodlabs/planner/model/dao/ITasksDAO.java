package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Subject;
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
public interface ITasksDAO extends JpaRepository<Task, Long> {

    Task findByTitle(String title);
    List<Task> findBySubject(Subject subject);

    @Query("from Task t where t.subject.layer = :layer")
    List<Task> findByLayer(@Param("layer") Layer layer);

}
