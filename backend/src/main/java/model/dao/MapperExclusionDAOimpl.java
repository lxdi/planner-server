package model.dao;

import model.entities.MapperExclusion;
import model.entities.SlotPosition;
import model.entities.Week;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapperExclusionDAOimpl implements IMapperExclusionDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void save(MapperExclusion me) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(me);
    }

    @Override
    public MapperExclusion findOne(long id) {
        return sessionFactory.getCurrentSession().get(MapperExclusion.class, id);
    }

    @Override
    public MapperExclusion getByWeekBySP(Week week, SlotPosition slotPosition) {
        return (MapperExclusion) sessionFactory.getCurrentSession()
                .createQuery("from MapperExclusion where week = :w and slotPosition = :sp")
                .setParameter("w", week)
                .setParameter("sp", slotPosition)
                .uniqueResult();
    }

    @Override
    public List<MapperExclusion> getByWeeksBySPs(List<Week> weeks, List<SlotPosition> slotPositions) {
        return sessionFactory.getCurrentSession()
                .createQuery("from MapperExclusion where week in :weeks and slotPosition in :slotposes")
                .setParameter("weeks", weeks)
                .setParameter("slotposes", slotPositions)
                .getResultList();
    }
}
