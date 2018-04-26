package model;

import model.entities.Task;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Alexander on 26.04.2018.
 */

@Service
@Transactional
public class TasksDao implements ITasksDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Task getById(long id) {
        return sessionFactory.getCurrentSession().get(Task.class, id);
    }

    @Override
    public void saveOrUpdate(Task task) {
        sessionFactory.getCurrentSession().saveOrUpdate(task);
    }
}
