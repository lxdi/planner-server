package model.dao;

import model.entities.Layer;
import model.entities.Subject;
import model.entities.Task;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SubjectDao implements ISubjectDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    ITasksDAO tasksDAO;

    @Override
    public void saveOrUpdate(Subject subject) {
        sessionFactory.getCurrentSession().saveOrUpdate(subject);
    }

    @Override
    public List<Subject> subjectsByLayer(Layer layer) {
        String hql = "from Subject sb where sb.layer = :layer";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("layer", layer);
        return query.list();
    }

    @Override
    public Subject getById(long id) {
        return sessionFactory.getCurrentSession().get(Subject.class, id);
    }

    @Override
    public void delete(long id) {
        Subject subject = this.getById(id);
        List<Task> tasks = tasksDAO.tasksBySubject(subject);
        if(tasks.size()>0){
            tasks.forEach(t->tasksDAO.delete(t.getId()));
        }
        sessionFactory.getCurrentSession().delete(subject);
    }
}
