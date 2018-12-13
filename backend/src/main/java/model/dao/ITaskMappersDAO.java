package model.dao;

import model.entities.Task;
import model.entities.TaskMapper;

public interface ITaskMappersDAO {

    void saveOrUpdate(TaskMapper taskMapper);
    TaskMapper taskMapperForTask(Task task);

}
