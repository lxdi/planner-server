package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Topic;

import java.util.List;

public interface ITopicDAO {

    void saveOrUpdate(Topic topic);
    Topic getById(long id);
    List<Topic> getByTaskId(long taskid);
}
