package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 26.04.2018.
 */

@Service
@Transactional
public class TasksDao implements ITasksDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Task getById(long id) {
        return entityManager.unwrap(Session.class).get(Task.class, id);
    }

    @Override
    public Task byTitle(String title) {
        return (Task) entityManager.unwrap(Session.class).createQuery("from Task t where t.title = :title")
                .setParameter("title", title).uniqueResult();
    }

    @Override
    public void saveOrUpdate(Task task) {
        entityManager.unwrap(Session.class).saveOrUpdate(task);
    }

    @Override
    public void delete(Task task) {
       entityManager.unwrap(Session.class).delete(task);
    }

    @Override
    public List<Task> allTasks() {
        return entityManager.unwrap(Session.class).createQuery("from Task").list();
    }

    @Override
    public List<Task> tasksBySubject(Subject subject) {
        return entityManager.unwrap(Session.class).createQuery("from Task t where t.subject = :subject")
                .setParameter("subject", subject).list();
    }

    @Override
    public List<Task> tasksByLayer(Layer layer) {
        String hql = "from Task t where t.subject.layer = :layer";
        Query query = entityManager.unwrap(Session.class).createQuery(hql).setParameter("layer", layer);
        return query.list();
    }

}
