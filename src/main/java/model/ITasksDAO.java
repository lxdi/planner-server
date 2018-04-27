package model;

import model.entities.Task;

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

}
