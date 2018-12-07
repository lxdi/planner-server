package model.dao;

import model.entities.Layer;
import model.entities.Mean;
import model.entities.HQuarter;
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

    @Autowired
    ILayerDAO layerDAO;

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
        //Mean meanToDelete = this.meanById(id);

        Mean meanToDelete = this.meanById(id);
        Mean prevMean = this.getPrevMean(meanToDelete);
        if(prevMean!=null ){
            if(meanToDelete.getNext()!=null){
                prevMean.setNext(meanToDelete.getNext());
            } else {
                prevMean.setNext(null);
            }
            this.saveOrUpdate(prevMean);
        }

        for(Layer dependedLayer : layerDAO.getLyersOfMean(meanToDelete)){
            layerDAO.delete(dependedLayer);
        }
        for(Mean childMean : this.getChildren(meanToDelete)){
            this.deleteMean(childMean.getId());
        }

        sessionFactory.getCurrentSession().delete(meanToDelete);

    }

    @Override
    public List<Mean> getChildren(Mean mean){
        return sessionFactory.getCurrentSession().createCriteria(Mean.class).add(Restrictions.eq("parent", mean)).list();
    }

    @Override
    public Mean meanByTitle(String title) {
        return (Mean) sessionFactory.getCurrentSession()
                .createCriteria(Mean.class).add(Restrictions.eq("title", title)).uniqueResult();
    }

    @Override
    public Mean getPrevMean(Mean mean) {
        return (Mean) sessionFactory.getCurrentSession()
                .createCriteria(Mean.class).add(Restrictions.eq("next", mean)).uniqueResult();
    }

//    @Override
//    public void assignQuarter(HQuarter HQuarter, Mean mean, Integer position) {
//        if(mean.getHquarter()==null || mean.getHquarter().getId()!= HQuarter.getId()) {
//            mean.setHquarter(HQuarter);
//            mean.setPosition(position);
//            validateByQuarter(mean);
//            this.saveOrUpdate(mean);
//        }
//    }

//    @Override
//    public void assignQuarter(@NotNull HQuarter quarter, @NotNull Mean mean, @NotNull Integer position) {
//        if (mean.getHquarter() == null || mean.getHquarter().getId() != quarter.getId()) {
//            int meansAlreadyAssigned = sessionFactory.getCurrentSession().createCriteria(Mean.class)
//                    .add(Restrictions.eq("realm", mean.getRealm()))
//                    .add(Restrictions.eq("quarter", quarter))
//                    .add(Restrictions.eq("position", position))
//                    .list().size();
//            if (meansAlreadyAssigned == 0) {
//                mean.setHquarter(quarter);
//                mean.setPosition(position);
//                this.saveOrUpdate(mean);
//            } else {
//                throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
//            }
//        }
//    }

    @Override
    public void validateMean(Mean mean){
        //validateByQuarter(mean);
    }

//    private void validateByQuarter(Mean mean){
//        if(mean.getHquarter()!=null){
//            if(mean.getPosition()==null){
//                throw new RuntimeException("Mean must contain position along with quarter");
//            }
//            List<Mean> meansAlreadyAssigned = sessionFactory.getCurrentSession().createCriteria(Mean.class)
//                    .add(Restrictions.eq("realm", mean.getRealm()))
//                    .add(Restrictions.eq("hquarter", mean.getHquarter()))
//                    .add(Restrictions.eq("position", mean.getPosition()))
//                    .list();
//            if(meansAlreadyAssigned.size()==0){
//                return;
//            }
//            if(meansAlreadyAssigned.size()==1) {
//                if(mean.getId()<1){
//                    throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
//                }
//                if (meansAlreadyAssigned.get(0).getId() != mean.getId()) {
//                    throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
//                }
//            }
//            if(meansAlreadyAssigned.size()>1){
//                throw new RuntimeException("Cannot assign mean to quarter, position is already occupied");
//            }
//        } else {
//            if(mean.getPosition()!=null){
//                throw new RuntimeException("Mean must contain position along with quarter");
//            }
//        }
//    }
}
