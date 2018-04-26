package model;

import model.entities.Task;

/**
 * Created by Alexander on 26.04.2018.
 */
public interface ITasksDAO {

    Task getById(long id);
    void saveOrUpdate(Task task);

}
