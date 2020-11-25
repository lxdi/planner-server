package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.TaskTesting;
import com.sogoodlabs.planner.model.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ITaskTestingDAO extends JpaRepository<TaskTesting, String> {

    @Query("from TaskTesting where task.id = :taskId")
    List<TaskTesting> getByTaskId(@Param("taskId") long taskid);

    List<TaskTesting> findByTask(Task task);

}
