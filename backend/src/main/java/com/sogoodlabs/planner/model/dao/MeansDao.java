package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 08.04.2018.
 */

@Service
@Transactional
public class MeansDao implements IMeansDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ILayerDAO layerDAO;

    @Override
    public List<Mean> getAllMeans() {
        return entityManager.unwrap(Session.class).createQuery("FROM Mean").list();
    }

    @Override
    public Mean meanById(long id) {
        return entityManager.unwrap(Session.class).get(Mean.class, id);
    }

    @Override
    public void saveOrUpdate(Mean mean) {
        entityManager.unwrap(Session.class).saveOrUpdate(mean);
    }

    @Override
    public void deleteMean(long id) {
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

        for(Mean childMean : this.getChildren(meanToDelete)){
            this.deleteMean(childMean.getId());
        }

        for(Layer dependedLayer : layerDAO.getLyersOfMean(meanToDelete)){
            layerDAO.delete(dependedLayer);
        }

        entityManager.unwrap(Session.class).delete(meanToDelete);

    }

    @Override
    public List<Mean> getChildren(Mean mean){
        String hql = "FROM Mean mean where mean.parent = :parent";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("parent", mean);
        return query.list();
    }

    @Override
    public Mean meanByTitle(String title) {
        String hql = "FROM Mean mean where mean.title = :title";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("title", title);
        return (Mean) query.uniqueResult();
    }

    @Override
    public Mean getPrevMean(Mean mean) {
        String hql = "FROM Mean mean where mean.next = :next";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("next", mean);
        return (Mean) query.uniqueResult();
    }

    @Override
    public Mean getLastOfChildren(Mean mean, Realm realm) {
        String hql = "FROM Mean mean where mean.realm = :realm and mean.next is null and "
                + (mean!=null?"mean.parent = :parent":"mean.parent is null");
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        if(mean!=null){
            query.setParameter("parent", mean);
        }
        query.setParameter("realm", realm);
        return (Mean) query.uniqueResult();
    }

    @Override
    public long assignsMeansCount(Mean mean) {
        String hql = "select count(*) from Slot s where s.mean = :mean";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("mean", mean);
        return (Long) query.uniqueResult();
    }

    @Override
    public List<Mean> meansAssignedToTarget(Target target) {
        String hql = "from Mean where :target in elements(targets)";
        Query query = this.entityManager.unwrap(Session.class).createQuery(hql)
                .setParameter("target", target);
        return query.list();
    }

    @Override
    public void validateMean(Mean mean){
        //validateByQuarter(mean);
    }
}
