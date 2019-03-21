package model.dao;

import model.entities.Task;
import model.entities.Topic;

import java.util.List;

public interface ITopicDAO {

    void saveOrUpdate(Topic topic);
    Topic getById(long id);
    List<Topic> getByTaskId(long taskid);
}
