package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.TaskTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ITaskTestingDAO extends JpaRepository<TaskTesting, String> {

    List<TaskTesting> findByTask(Task task);

    @Query("from TaskTesting where task.id = :taskId")
    List<TaskTesting> getByTaskId(@Param("taskId") long taskid);

    @Query("from TaskTesting where task.layer.mean = :mean")
    List<TaskTesting> findByMean(@Param("mean") Mean mean);
}
