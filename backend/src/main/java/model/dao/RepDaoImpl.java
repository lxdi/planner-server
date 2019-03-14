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
    public List<Repetition> getWithPlanDateInRange(Date from, Date to) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Repetition where planDate >= :from and planDate <= :to")
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
