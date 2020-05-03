package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.TaskTesting;
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
public class TaskTestingDAOImpl implements ITaskTestingDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public TaskTesting findOne(long id) {
        return this.entityManager.unwrap(Session.class).get(TaskTesting.class, id);
    }

    @Override
    public void save(TaskTesting taskTesting) {
        this.entityManager.unwrap(Session.class).saveOrUpdate(taskTesting);
    }

    @Override
    public void delete(long id) {
        this.entityManager.unwrap(Session.class).delete(this.findOne(id));
    }

    @Override
    public List<TaskTesting> getByTask(long taskid) {
        return this.entityManager.unwrap(Session.class)
                .createQuery("from TaskTesting where task.id = :id")
                .setParameter("id", taskid)
                .getResultList();
    }
}
