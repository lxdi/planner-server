package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ITopicDAO extends JpaRepository<Topic, String> {

    @Query("from Topic where task.id = :taskId")
    List<Topic> getByTaskId(@Param("taskId") String taskid);

    List<Topic> findByTask(Task task);
}
