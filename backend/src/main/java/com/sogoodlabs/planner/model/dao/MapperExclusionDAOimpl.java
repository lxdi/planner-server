package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.MapperExclusion;
import com.sogoodlabs.planner.model.entities.SlotPosition;
import com.sogoodlabs.planner.model.entities.Week;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class MapperExclusionDAOimpl implements IMapperExclusionDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void save(MapperExclusion me) {
        entityManager.unwrap(Session.class).saveOrUpdate(me);
    }

    @Override
    public MapperExclusion findOne(long id) {
        return entityManager.unwrap(Session.class).get(MapperExclusion.class, id);
    }

    @Override
    public MapperExclusion getByWeekBySP(Week week, SlotPosition slotPosition) {
        return (MapperExclusion) entityManager.unwrap(Session.class)
                .createQuery("from MapperExclusion where week = :w and slotPosition = :sp")
                .setParameter("w", week)
                .setParameter("sp", slotPosition)
                .uniqueResult();
    }

    @Override
    public List<MapperExclusion> getByWeeksBySPs(List<Week> weeks, List<SlotPosition> slotPositions) {
        return entityManager.unwrap(Session.class)
                .createQuery("from MapperExclusion where week in :weeks and slotPosition in :slotposes")
                .setParameter("weeks", weeks)
                .setParameter("slotposes", slotPositions)
                .getResultList();
    }

    @Override
    public void deleteBySlotPositions(List<SlotPosition> sps) {
        entityManager.unwrap(Session.class)
                .createQuery("delete from MapperExclusion where slotPosition in :slotposes")
                .setParameter("slotposes", sps)
                .executeUpdate();
    }
//    @Override
//    public List<MapperExclusion> getBySlotPositions(List<SlotPosition> sps) {
//        return entityManager.unwrap(Session.class)
//                .createQuery("from MapperExclusion where slotPosition in :slotposes")
//                .setParameter("slotposes", sps)
//                .getResultList();

//    }
}
