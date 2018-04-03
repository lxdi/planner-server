package model;

import model.entities.Target;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Alexander on 10.03.2018.
 */

@Service
@Transactional
public class TargetsDao implements ITargetsDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Target> topTargets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Target> allTargets() {
//        CriteriaBuilder builder =  sessionFactory.getCurrentSession().getCriteriaBuilder();
//        CriteriaQuery<Target> criteria = builder.createQuery(Target.class);
        return sessionFactory.getCurrentSession().createCriteria(Target.class).list();
    }

    @Override
    public Target targetById(long id) {
        return sessionFactory.getCurrentSession().get(Target.class, id);
    }

    @Override
    public void saveOrUpdate(Target target) {
        sessionFactory.getCurrentSession().saveOrUpdate(target);
    }

    @Override
    public void deleteTarget(long id) {
        sessionFactory.getCurrentSession().delete(this.targetById(id));
    }
}
