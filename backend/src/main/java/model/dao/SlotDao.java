package model.dao;

import model.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SlotDao implements ISlotDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Slot slot) {
        sessionFactory.getCurrentSession().saveOrUpdate(slot);
    }

    @Override
    public void saveOrUpdate(SlotPosition slotPosition) {
        sessionFactory.getCurrentSession().saveOrUpdate(slotPosition);
    }

    @Override
    public Slot getById(long id) {
        return this.sessionFactory.getCurrentSession().get(Slot.class, id);
    }

    @Override
    public Slot getByHquarterAndPosition(HQuarter hQuarter, int position) {
        String hql = "FROM Slot slot WHERE slot.hquarter = :hquarter and slot.position = :position";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("hquarter", hQuarter);
        query.setParameter("position", position);
        return (Slot) query.uniqueResult();
    }

    @Override
    public SlotPosition getSlotPositionById(long id) {
        return this.sessionFactory.getCurrentSession().get(SlotPosition.class, id);
    }

    @Override
    public List<Slot> getSlotsForHquarter(HQuarter hquarter) {
        String hql = "FROM Slot slot WHERE slot.hquarter = :hquarter";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("hquarter", hquarter);
        query.setCacheable(true);
        return query.list();
    }

    @Override
    public List<Slot> getSlotsForHquarters(List<HQuarter> hquarters) {
        String hql = "FROM Slot slot WHERE slot.hquarter in :hquarters";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("hquarters", hquarters);
        query.setCacheable(true);
        return query.list();
    }

    @Override
    public List<Slot> slotsAfter(Slot slot) {
        assert slot.getMean()!=null;
        String hql = "from Slot s where s.mean = :mean and s.hquarter.startWeek.startDay > :startDay order by hquarter.startWeek.startDay asc";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("mean", slot.getMean());
        query.setParameter("startDay", slot.getHquarter().getStartWeek().getStartDay());
        return query.list();
    }

    @Override
    public List<SlotPosition> getSlotPositionsForSlot(Slot slot) {
        String hql = "FROM SlotPosition sp WHERE sp.slot= :slot";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("slot", slot);
        return query.list();
    }

    @Override
    public SlotPosition getSlotPosition(Slot slot, DaysOfWeek dayOfWeek, int position) {
        return (SlotPosition) sessionFactory.getCurrentSession()
                .createQuery("from SlotPosition sp from sp.slot = :slot and sp.dayOfWeek and sp.position")
                .uniqueResult();
    }

    @Override
    public List<Slot> slotsWithMean(Mean mean) {
        return sessionFactory.getCurrentSession().createQuery("from Slot where mean = :mean " +
                "order by hquarter.startWeek.startDay asc, position asc")
                .setParameter("mean", mean).list();
    }
}
