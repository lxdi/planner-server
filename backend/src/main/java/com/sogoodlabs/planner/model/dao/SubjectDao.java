package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Subject;
import com.sogoodlabs.planner.model.entities.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
@Service
public class SubjectDao implements ISubjectDAO {
    
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ITasksDAO tasksDAO;

    @Override
    public void saveOrUpdate(Subject subject) {
        entityManager.unwrap(Session.class).saveOrUpdate(subject);
    }

    @Override
    public List<Subject> subjectsByLayer(Layer layer) {
        String hql = "from Subject sb where sb.layer = :layer";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("layer", layer);
        return query.list();
    }

    @Override
    public Subject getById(long id) {
        return entityManager.unwrap(Session.class).get(Subject.class, id);
    }

    @Override
    public void delete(Subject subject) {
        entityManager.unwrap(Session.class).delete(subject);
    }
}
