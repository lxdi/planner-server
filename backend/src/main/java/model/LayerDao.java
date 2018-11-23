package model;

import model.entities.Layer;
import model.entities.Mean;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LayerDao implements ILayerDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Layer> getLyersOfMean(Mean mean) {
        return sessionFactory.getCurrentSession().createCriteria(Layer.class)
                .add(Restrictions.eq("mean", mean))
                .list();
    }

    @Override
    public void saveOrUpdate(Layer layer) {
        sessionFactory.getCurrentSession().saveOrUpdate(layer);
    }
}
