package model.dao;

import model.entities.Layer;
import model.entities.Mean;
import model.entities.Slot;
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

    @Autowired
    ILayerDAO layerDAO;

    @Override
    public List<Layer> getLyersOfMean(Mean mean) {
        String hql = "from Layer lr where lr.mean = :mean order by lr.priority asc";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("mean", mean);
        return query.list();
    }

    @Override
    public Layer getNextLayerToSchedule(Mean mean) {
        long assignsCount = meansDAO.assignsMeansCount(mean);
        Layer result = getLayerAtPriority(mean, (int)(assignsCount+1));
        return result;
    }

    @Override
    public Layer getLayerToScheduleForSlot(Slot slot) {
        String hql = "select count(*) from Slot where " +
                "((hquarter.startWeek.startDay < :startDay) or (hquarter.startWeek.startDay = :startDay and position<:position) )" +
                "and mean = :mean";
        long slotsBeforeCount = (long) sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("startDay", slot.getHquarter().getStartWeek().getStartDay())
                .setParameter("position", slot.getPosition())
                .setParameter("mean", slot.getMean())
                .uniqueResult();
        return layerDAO.getLayerAtPriority(slot.getMean(), (int)(slotsBeforeCount+1));
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

    @Override
    public long taskCountInLayer(Layer layer) {
        return (long) sessionFactory.getCurrentSession()
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
        return sessionFactory.getCurrentSession().get(Layer.class, id);
    }

    @Override
    public void saveOrUpdate(Layer layer) {
        sessionFactory.getCurrentSession().saveOrUpdate(layer);
    }
}
