package model;

import model.entities.Mean;
import model.entities.Target;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
        Target targetToDelete = this.targetById(id);

        //List<Mean> means = sessionFactory.getCurrentSession().createCriteria(Mean.class).add(Restrictions.in("targets", targetToDelete)).list();
        Criteria c = sessionFactory.getCurrentSession().createCriteria(Mean.class, "mean");
        c.createAlias("mean.targets", "target");
        c.add(Restrictions.eq("target.id", targetToDelete.getId()));
        List<Mean> means = c.list();
        for(Mean mean: means){
            if(mean.getTargets().size()==1){
                meansDAO.deleteMean(mean.getId());
            } else {
                Iterator iterator = mean.getTargets().iterator();
                while (iterator.hasNext()){
                    Target curTarget = (Target) iterator.next();
                    if(curTarget.getId()==targetToDelete.getId()){
                        iterator.remove();
                    }
                }
            }
        }

        for(Target target : this.getChildren(targetToDelete)){
            this.deleteTarget(target.getId());
        }
        sessionFactory.getCurrentSession().delete(targetToDelete);
    }

    public List<Target> getChildren(Target target){
        return sessionFactory.getCurrentSession().createCriteria(Target.class).add(Restrictions.eq("parent", target)).list();
    }

    @Override
    public Target getTargetByTitle(String title) {
        List<Target> result = sessionFactory.getCurrentSession().createCriteria(Target.class).add(Restrictions.eq("title", title)).list();
        return result.size()>0? result.get(0):null;
    }
}
