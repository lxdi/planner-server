package model;

import model.IMeansDAO;
import model.entities.Mean;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Alexander on 08.04.2018.
 */

@Service
@Transactional
public class MeansDao implements IMeansDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Mean> getAllMeans() {
        return sessionFactory.getCurrentSession().createCriteria(Mean.class).list();
    }

    @Override
    public Mean meanById(long id) {
        return sessionFactory.getCurrentSession().get(Mean.class, id);
    }

    @Override
    public void saveOrUpdate(Mean mean) {
        sessionFactory.getCurrentSession().saveOrUpdate(mean);
    }

    @Override
    public void deleteMean(long id) {
        Mean meanToDelete = this.meanById(id);
        for(Mean target : this.getChildren(meanToDelete)){
            this.deleteMean(target.getId());
        }
        sessionFactory.getCurrentSession().delete(meanToDelete);
    }

    @Override
    public List<Mean> getChildren(Mean mean){
        return sessionFactory.getCurrentSession().createCriteria(Mean.class).add(Restrictions.eq("parent", mean)).list();
    }

    @Override
    public Mean meanByTitle(String title) {
        return (Mean) sessionFactory.getCurrentSession().createCriteria(Mean.class).add(Restrictions.eq("title", title)).uniqueResult();
    }
}
