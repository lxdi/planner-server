package model;

import com.sun.istack.internal.NotNull;
import model.entities.Mean;
import model.entities.Quarter;
import model.entities.Realm;
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

    @Override
    public void assignQuarter(@NotNull Quarter quarter, @NotNull Mean mean, @NotNull Integer position) {
        if (mean.getQuarter() == null || mean.getQuarter().getId() != quarter.getId()) {
            int meansAlreadyAssigned = sessionFactory.getCurrentSession().createCriteria(Mean.class)
                    .add(Restrictions.eq("realm", mean.getRealm()))
                    .add(Restrictions.eq("quarter", quarter))
                    .add(Restrictions.eq("position", position))
                    .list().size();
            if (meansAlreadyAssigned == 0) {
                mean.setQuarter(quarter);
                mean.setPosition(position);
                this.saveOrUpdate(mean);
            } else {
                throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
            }
        }
    }
}
