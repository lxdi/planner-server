package model;

import model.entities.Mean;
import model.entities.Quarter;
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
    public void assignQuarter(Quarter quarter, Mean mean, Integer position) {
        if(mean.getQuarter()==null || mean.getQuarter().getId()!=quarter.getId()) {
            mean.setQuarter(quarter);
            mean.setPosition(position);
            validateByQuarter(mean);
            this.saveOrUpdate(mean);
        }
    }

//    @Override
//    public void assignQuarter(@NotNull Quarter quarter, @NotNull Mean mean, @NotNull Integer position) {
//        if (mean.getQuarter() == null || mean.getQuarter().getId() != quarter.getId()) {
//            int meansAlreadyAssigned = sessionFactory.getCurrentSession().createCriteria(Mean.class)
//                    .add(Restrictions.eq("realm", mean.getRealm()))
//                    .add(Restrictions.eq("quarter", quarter))
//                    .add(Restrictions.eq("position", position))
//                    .list().size();
//            if (meansAlreadyAssigned == 0) {
//                mean.setQuarter(quarter);
//                mean.setPosition(position);
//                this.saveOrUpdate(mean);
//            } else {
//                throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
//            }
//        }
//    }

    @Override
    public void validateMean(Mean mean){
        validateByQuarter(mean);
    }

    private void validateByQuarter(Mean mean){
        if(mean.getQuarter()!=null){
            if(mean.getPosition()==null){
                throw new RuntimeException("Mean must contain position along with quarter");
            }
            List<Mean> meansAlreadyAssigned = sessionFactory.getCurrentSession().createCriteria(Mean.class)
                    .add(Restrictions.eq("realm", mean.getRealm()))
                    .add(Restrictions.eq("quarter", mean.getQuarter()))
                    .add(Restrictions.eq("position", mean.getPosition()))
                    .list();
            if(meansAlreadyAssigned.size()==0){
                return;
            }
            if(meansAlreadyAssigned.size()==1) {
                if(mean.getId()<1){
                    throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
                }
                if (meansAlreadyAssigned.get(0).getId() != mean.getId()) {
                    throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
                }
            }
            if(meansAlreadyAssigned.size()>1){
                throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
            }
        } else {
            if(mean.getPosition()!=null){
                throw new RuntimeException("Mean must contain position along with quarter");
            }
        }
    }
}
