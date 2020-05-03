package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.TaskTesting;

import java.util.List;

public interface ITaskTestingDAO {

    TaskTesting findOne(long id);
    void save(TaskTesting taskTesting);
    void delete(long id);
    List<TaskTesting> getByTask(long taskid);

}
