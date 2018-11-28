package model.dao;

import model.entities.Layer;
import model.entities.Subject;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SubjectDao implements ISubjectDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Subject subject) {
        sessionFactory.getCurrentSession().saveOrUpdate(subject);
    }

    @Override
    public List<Subject> subjectsByLayer(Layer layer) {
        return sessionFactory.getCurrentSession().createCriteria(Subject.class)
                .add(Restrictions.eq("layer", layer))
                .list();
    }
}
