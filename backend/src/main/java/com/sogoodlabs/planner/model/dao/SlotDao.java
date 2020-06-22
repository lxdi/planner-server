package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;
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
public class SlotDao implements ISlotDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void saveOrUpdate(Slot slot) {
        entityManager.unwrap(Session.class).saveOrUpdate(slot);
    }

    @Override
    public void saveOrUpdate(SlotPosition slotPosition) {
        entityManager.unwrap(Session.class).saveOrUpdate(slotPosition);
    }

    @Override
    public Slot getById(long id) {
        return this.entityManager.unwrap(Session.class).get(Slot.class, id);
    }

    @Override
    public Slot getByHquarterAndPosition(HQuarter hQuarter, int position) {
        String hql = "FROM Slot slot WHERE slot.hquarter = :hquarter and slot.position = :position";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("hquarter", hQuarter);
        query.setParameter("position", position);
        return (Slot) query.uniqueResult();
    }

    @Override
    public SlotPosition getSlotPositionById(long id) {
        return this.entityManager.unwrap(Session.class).get(SlotPosition.class, id);
    }

    @Override
    public List<Slot> getSlotsForHquarter(HQuarter hquarter) {
        String hql = "FROM Slot slot WHERE slot.hquarter = :hquarter";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("hquarter", hquarter);
        query.setCacheable(true);
        return query.list();
    }

    @Override
    public List<Slot> getSlotsForHquarters(List<HQuarter> hquarters) {
        String hql = "FROM Slot slot WHERE slot.hquarter in :hquarters";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("hquarters", hquarters);
        query.setCacheable(true);
        return query.list();
    }

    @Override
    public List<SlotPosition> getSlotPositionsForSlot(Slot slot) {
        String hql = "FROM SlotPosition sp WHERE sp.slot= :slot";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("slot", slot);
        return query.list();
    }

    @Override
    public SlotPosition getSlotPosition(Slot slot, DaysOfWeek dayOfWeek, int position) {
        return (SlotPosition) entityManager.unwrap(Session.class)
                .createQuery("from SlotPosition sp from sp.slot = :slot and sp.dayOfWeek and sp.position")
                .uniqueResult();
    }

    @Override
    public List<Slot> slotsWithMean(Mean mean) {
        return entityManager.unwrap(Session.class).createQuery("from Slot where mean = :mean " +
                "order by hquarter.startWeek.startDay asc, position asc")
                .setParameter("mean", mean).list();
    }

    @Override
    public List<Slot> slotsWithLayers(List<Layer> layers) {
        String hql = "FROM Slot s WHERE s.layer in :layers";
        Query query = entityManager.unwrap(Session.class).createQuery(hql);
        query.setParameter("layers", layers);
        return query.list();
    }
}