package model.dao;

import model.entities.HQuarter;
import model.entities.Slot;
import model.entities.SlotPosition;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
    public SlotPosition getSlotPositionById(long id) {
        return this.sessionFactory.getCurrentSession().get(SlotPosition.class, id);
    }

    @Override
    public List<Slot> getSlotsForHquarter(HQuarter hquarter) {
        return sessionFactory.getCurrentSession().createCriteria(Slot.class)
                .add(Restrictions.eq("hquarter", hquarter)).list();
    }

    @Override
    public List<SlotPosition> getSlotPositionsForSlot(Slot slot) {
        return sessionFactory.getCurrentSession().createCriteria(SlotPosition.class)
                .add(Restrictions.eq("slot", slot)).list();
    }
}
