package model.dao;

import model.entities.Layer;
import model.entities.Mean;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LayerDao implements ILayerDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    IMeansDAO meansDAO;

    @Override
    public List<Layer> getLyersOfMean(Mean mean) {
        String hql = "from Layer lr where lr.mean = :mean";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("mean", mean);
        return query.list();
//        return sessionFactory.getCurrentSession().createCriteria(Layer.class)
//                .add(Restrictions.eq("mean", mean))
//                .list();
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
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
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
        sessionFactory.getCurrentSession().delete(layer);
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
        return sessionFactory.getCurrentSession().get(Layer.class, id);
    }

    @Override
    public void saveOrUpdate(Layer layer) {
        sessionFactory.getCurrentSession().saveOrUpdate(layer);
    }
}
