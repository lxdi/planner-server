package model.dao;

import model.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 26.04.2018.
 */

@Service
@Transactional
public class TasksDao implements ITasksDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Override
    public Task getById(long id) {
        return sessionFactory.getCurrentSession().get(Task.class, id);
    }

    @Override
    public Task byTitle(String title) {
        return (Task) sessionFactory.getCurrentSession().createQuery("from Task t where t.title = :title")
                .setParameter("title", title).uniqueResult();
    }

    @Override
    public void saveOrUpdate(Task task) {
        sessionFactory.getCurrentSession().saveOrUpdate(task);
        handleRemovedTopics(task);
    }

    private void handleRemovedTopics(Task task){
        String queryString = "delete from Topic t where t.task = :task";
        if(task.getTopics().size()>0){
            queryString = queryString + " and t not in :topicsToSurvive";
        }
        Query query = sessionFactory.getCurrentSession().createQuery(queryString);
        query.setParameter("task", task);
        if (task.getTopics().size() > 0) {
            query.setParameter("topicsToSurvive", task.getTopics());
        }
        query.executeUpdate();
    }

    @Override
    public void delete(long id) {
        Task task = this.getById(id);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        if(taskMapper!=null){
            taskMappersDAO.delete(taskMapper);
        }
        sessionFactory.getCurrentSession().delete(task);
        //sessionFactory.getCurrentSession().delete(this.getById(id));
    }

    @Override
    public List<Task> allTasks() {
        return sessionFactory.getCurrentSession().createQuery("from Task").list();
    }

    @Override
    public List<Task> tasksBySubject(Subject subject) {
        return sessionFactory.getCurrentSession().createQuery("from Task t where t.subject = :subject")
                .setParameter("subject", subject).list();
    }

    @Override
    public List<Task> tasksByLayer(Layer layer) {
        String hql = "from Task t where t.subject.layer = :layer";
        Query query = sessionFactory.getCurrentSession().createQuery(hql).setParameter("layer", layer);
        return query.list();
    }

    @Override
    public int getRepetitions(long taskid) {
        return (int) sessionFactory.getCurrentSession()
                .createQuery("select repetitions from TaskMapper where task.id = :id")
                .setParameter("id", taskid).uniqueResult();
    }

    @Override
    public void updateRepetitions(long taskid, int reps) {
        this.sessionFactory.getCurrentSession()
                .createQuery("update TaskMapper set repetitions=:reps where task.id = :taskid")
                .setParameter("reps", reps)
                .setParameter("taskid", taskid).executeUpdate();
    }

}
