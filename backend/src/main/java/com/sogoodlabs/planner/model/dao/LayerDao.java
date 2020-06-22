package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Slot;
import com.sogoodlabs.planner.model.entities.Subject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class LayerDao implements ILayerDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Override
    public List<Layer> getLyersOfMean(Mean mean) {
        String hql = "from Layer lr where lr.mean = :mean order by lr.priority asc";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("mean", mean);
        return query.list();
    }

    @Override
    public List<Layer> getLyersOfMeans(List<Mean> means) {
        String hql = "from Layer where mean in :means";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("means", means);
        return query.list();
    }

    @Override
    public Layer getNextLayerToSchedule(Mean mean) {
        long assignsCount = meansDAO.assignsMeansCount(mean);
        Layer result = getLayerAtPriority(mean, (int)(assignsCount+1));
        return result;
    }

    @Override
    public Layer getLayerAtPriority(Mean mean, int priority) {
        String hql = "from Layer l where l.mean = :mean and l.priority = :priority";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("mean", mean);
        query.setParameter("priority", priority);
        return (Layer) query.uniqueResult();
    }

    @Override
    public Layer create(Mean mean) {
        Layer layer = new Layer(mean, getNextPriority(mean));
        return layer;
    }

    @Override
    public void delete(Layer layer) {
        List<Subject> subjects = subjectDAO.subjectsByLayer(layer);
        if(subjects.size()>0){
            subjects.forEach(s->subjectDAO.delete(s.getId()));
        }
        entityManager.unwrap(Session.class).delete(layer);
    }

    @Override
    public long taskCountInLayer(Layer layer) {
        return (long) entityManager.unwrap(Session.class)
                .createQuery("select count(*) from Task where subject.layer = :layer")
                .setParameter("layer", layer)
                .uniqueResult();
    }

    private int getNextPriority(Mean mean){
        //TODO optimize
        int max = 0;
        for(Layer layer : this.getLyersOfMean(mean)){
            if(layer.getPriority()>max){
                max = layer.getPriority();
            }
        }
        return max+1;
    }

    @Override
    public Layer layerById(long id) {
        return entityManager.unwrap(Session.class).get(Layer.class, id);
    }

    @Override
    public void saveOrUpdate(Layer layer) {
        entityManager.unwrap(Session.class).saveOrUpdate(layer);
    }
}