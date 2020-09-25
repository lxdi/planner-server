package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        unassignMeans(targetToDelete);
        handlePrevForDeleting(targetToDelete);

        for(Target target : this.getChildren(targetToDelete)){
            this.deleteTarget(target.getId());
        }
        entityManager.unwrap(Session.class).delete(targetToDelete);
    }

    private void unassignMeans(Target target) {
        meansDAO.meansAssignedToTarget(target).forEach(mean -> {
            mean.getTargets().removeIf(curTarget -> curTarget.getId() == target.getId());
            meansDAO.saveOrUpdate(mean);
        });
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
        String hql = "FROM Target where realm = :realm and next is null and parent = :parent";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("parent", targetParent);
//        if(targetParent!=null){
//        }
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
