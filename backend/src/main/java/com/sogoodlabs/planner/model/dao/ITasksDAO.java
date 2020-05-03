package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Subject;
import com.sogoodlabs.planner.model.entities.Task;

import java.util.List;

/**
 * Created by Alexander on 26.04.2018.
 */
public interface ITasksDAO {

    Task getById(long id);
    Task byTitle(String title);
    void saveOrUpdate(Task task);
    void delete(long id);
    List<Task> allTasks();
    List<Task> tasksBySubject(Subject subject);
    List<Task> tasksByLayer(Layer layer);

}
