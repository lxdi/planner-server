package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alexander on 10.03.2018.
 */

@Service
@Transactional
public class TargetsDao implements ITargetsDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    IMeansDAO meansDAO;
    

    @Override
    public List<Target> topTargets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Target> allTargets() {
        return entityManager.unwrap(Session.class).createQuery("from Target").list();
    }

    @Override
    public Target targetById(long id) {
        return entityManager.unwrap(Session.class).get(Target.class, id);
    }

    @Override
    public void saveOrUpdate(Target target) {
        entityManager.unwrap(Session.class).saveOrUpdate(target);
    }

    @Override
    public void deleteTarget(long id) {
        Target targetToDelete = this.targetById(id);
        //deleteDependedMeans(targetToDelete);
        anassignMeans(targetToDelete);
        handlePrevForDeleting(targetToDelete);

        for(Target target : this.getChildren(targetToDelete)){
            this.deleteTarget(target.getId());
        }
        entityManager.unwrap(Session.class).delete(targetToDelete);
    }

    private void anassignMeans(Target target){
        List<Mean> means = meansDAO.meansAssignedToTarget(target);
        if(means.size()>0){
            means.forEach(mean->{
                Iterator<Target> targetIterator = mean.getTargets().iterator();
                while(targetIterator.hasNext()){
                    Target curTarget = targetIterator.next();
                    if(curTarget.getId()==target.getId()){
                        targetIterator.remove();
                    }
                }
                meansDAO.saveOrUpdate(mean);
            });
        }
    }

    private void handlePrevForDeleting(Target target){
        Target prevTarget = this.getPrevTarget(target);
        if(prevTarget!=null ){
            if(target.getNext()!=null){
                prevTarget.setNext(target.getNext());
            } else {
                prevTarget.setNext(null);
            }
            this.saveOrUpdate(prevTarget);
        }
    }

    public List<Target> getChildren(Target target){
        String hql = "from Target t where t.parent = :target";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("target", target);
        return query.list();
        //return entityManager.unwrap(Session.class).createCriteria(Target.class).add(Restrictions.eq("parent", target)).list();
    }

    @Override
    public Target getTargetByTitle(String title) {
        List<Target> result = entityManager.unwrap(Session.class).createQuery("from Target t where t.title = :title")
                .setParameter("title", title).list();
        return result.size()>0? result.get(0):null;
    }

    @Override
    public Target getPrevTarget(Target target) {
        String hql = "FROM Target where next = :next";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("next", target);
        return (Target) query.uniqueResult();
    }

    @Override
    public Target getLastOfChildren(Target targetParent, Realm realm) {
        String hql = "FROM Target where realm = :realm and next is null and "
                + (targetParent!=null?"parent = :parent":"parent is null");
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        if(targetParent!=null){
            query.setParameter("parent", targetParent);
        }
        query.setParameter("realm", realm);
        return (Target) query.uniqueResult();
    }

    @Override
    public boolean isLeafTarget(Target target) {
        String hql = "select count(*) from Target where parent = :target";
        Query query = entityManager.unwrap(Session.class).createQuery(hql)
                .setParameter("target", target);
        return (long)query.uniqueResult()==0;
    }
}
