package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Topic;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public List<Topic> getByTaskId(long taskid) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Topic where task.id = :taskid")
                .setParameter("taskid", taskid)
                .getResultList();
    }
}
