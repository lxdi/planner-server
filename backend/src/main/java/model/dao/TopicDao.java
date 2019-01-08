package model.dao;

import model.entities.Topic;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TopicDao implements ITopicDAO{

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Topic topic) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(topic);
    }

    @Override
    public Topic getById(long id) {
        return this.sessionFactory.getCurrentSession().get(Topic.class, id);
    }
}
