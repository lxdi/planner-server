package model.dao;

import model.entities.Repetition;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class RepDaoImpl implements IRepDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Repetition findOne(long id) {
        return this.sessionFactory.getCurrentSession().get(Repetition.class, id);
    }

    @Override
    public void save(Repetition repetition) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(repetition);
    }

    @Override
    public List<Repetition> getRepsbySpacedRepId(long srId) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Repetition where spacedRepetitions.id = :srId")
                .setParameter("srId", srId)
                .getResultList();
    }

    @Override
    public List<Repetition> getUnFinishedWithPlanDateInRange(Date from, Date to) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Repetition where planDate >= :from and planDate <= :to and factDate is null order by planDate asc")
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
