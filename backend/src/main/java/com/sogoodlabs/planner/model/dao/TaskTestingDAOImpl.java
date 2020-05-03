package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.TaskTesting;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class TaskTestingDAOImpl implements ITaskTestingDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public TaskTesting findOne(long id) {
        return this.sessionFactory.getCurrentSession().get(TaskTesting.class, id);
    }

    @Override
    public void save(TaskTesting taskTesting) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(taskTesting);
    }

    @Override
    public void delete(long id) {
        this.sessionFactory.getCurrentSession().delete(this.findOne(id));
    }

    @Override
    public List<TaskTesting> getByTask(long taskid) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from TaskTesting where task.id = :id")
                .setParameter("id", taskid)
                .getResultList();
    }
}
