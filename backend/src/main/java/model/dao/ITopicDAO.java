package model.dao;

import model.entities.Topic;

public interface ITopicDAO {

    void saveOrUpdate(Topic topic);
    Topic getById(long id);
}
