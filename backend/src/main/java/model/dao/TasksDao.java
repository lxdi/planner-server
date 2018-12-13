package model.dao;

import model.entities.Subject;
import model.entities.Task;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Task byTitle(String title) {
        return (Task) sessionFactory.getCurrentSession().createQuery("from Task t where t.title = :title")
                .setParameter("title", title).uniqueResult();
        //return (Task) sessionFactory.getCurrentSession().createCriteria(Task.class).add(Restrictions.eq("title", title)).uniqueResult();
    }

    @Override
    public void saveOrUpdate(Task task) {
        sessionFactory.getCurrentSession().saveOrUpdate(task);
    }

    @Override
    public void delete(long id) {
        sessionFactory.getCurrentSession().delete(this.getById(id));
    }

    @Override
    public List<Task> allTasks() {
        return sessionFactory.getCurrentSession().createQuery("from Task").list();
        //return sessionFactory.getCurrentSession().createCriteria(Task.class).list();
    }

    @Override
    public List<Task> tasksBySubject(Subject subject) {
        return sessionFactory.getCurrentSession().createQuery("from Task t where t.subject = :subject")
                .setParameter("subject", subject).list();
//        return sessionFactory.getCurrentSession().createCriteria(Task.class)
//                .add(Restrictions.eq("subject", subject))
//                .list();
    }

}
