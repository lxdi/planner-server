package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Topic;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class TopicDao implements ITopicDAO{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void saveOrUpdate(Topic topic) {
        this.entityManager.unwrap(Session.class).saveOrUpdate(topic);
    }

    @Override
    public Topic getById(long id) {
        return this.entityManager.unwrap(Session.class).get(Topic.class, id);
    }

    @Override
    public List<Topic> getByTaskId(long taskid) {
        return this.entityManager.unwrap(Session.class)
                .createQuery("from Topic where task.id = :taskid")
                .setParameter("taskid", taskid)
                .getResultList();
    }
}
