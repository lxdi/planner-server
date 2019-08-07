package model.dao;

import model.entities.Mean;
import model.entities.Realm;
import model.entities.Target;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Alexander on 10.03.2018.
 */

@Service
@Transactional
public class TargetsDao implements ITargetsDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    IMeansDAO meansDAO;

    @Override
    public List<Target> topTargets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Target> allTargets() {
        return sessionFactory.getCurrentSession().createQuery("from Target").list();
        //return sessionFactory.getCurrentSession().createCriteria(Target.class).list();
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
        Target targetToDelete = this.targetById(id);
        deleteDependedMeans(targetToDelete);
        handlePrevForDeleting(targetToDelete);

        for(Target target : this.getChildren(targetToDelete)){
            this.deleteTarget(target.getId());
        }
        sessionFactory.getCurrentSession().delete(targetToDelete);
    }

    private void deleteDependedMeans(Target target){
        String hql = "select m from Mean m join m.targets t where t = :target";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("target", target);
        List<Mean> means = query.list();

        for(Mean mean: means){
            if(mean.getTargets().size()==1){
                meansDAO.deleteMean(mean.getId());
            } else {
                Iterator iterator = mean.getTargets().iterator();
                while (iterator.hasNext()){
                    Target curTarget = (Target) iterator.next();
                    if(curTarget.getId()==target.getId()){
                        iterator.remove();
                    }
                }
            }
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
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("target", target);
        return query.list();
        //return sessionFactory.getCurrentSession().createCriteria(Target.class).add(Restrictions.eq("parent", target)).list();
    }

    @Override
    public Target getTargetByTitle(String title) {
        List<Target> result = sessionFactory.getCurrentSession().createQuery("from Target t where t.title = :title")
                .setParameter("title", title).list();
        return result.size()>0? result.get(0):null;
    }

    @Override
    public Target getPrevTarget(Target target) {
        String hql = "FROM Target where next = :next";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("next", target);
        return (Target) query.uniqueResult();
    }

    @Override
    public Target getLastOfChildren(Target targetParent, Realm realm) {
        String hql = "FROM Target where realm = :realm and next is null and "
                + (targetParent!=null?"parent = :parent":"parent is null");
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        if(targetParent!=null){
            query.setParameter("parent", targetParent);
        }
        query.setParameter("realm", realm);
        return (Target) query.uniqueResult();
    }

    @Override
    public boolean isLeafTarget(Target target) {
        String hql = "select count(*) from Target where parent = :target";
        Query query = sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("target", target);
        return (long)query.uniqueResult()==0;
    }
}
