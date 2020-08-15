package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.SpacedRepetitions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class RepDaoImpl implements IRepDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Repetition findOne(long id) {
        return this.entityManager.unwrap(Session.class).get(Repetition.class, id);
    }

    @Override
    public void save(Repetition repetition) {
        this.entityManager.unwrap(Session.class).saveOrUpdate(repetition);
    }

    @Override
    public List<Repetition> getRepsbySpacedRepId(long srId) {
        return this.entityManager.unwrap(Session.class)
                .createQuery("from Repetition where spacedRepetitions.id = :srId")
                .setParameter("srId", srId)
                .getResultList();
    }

    @Override
    public List<Repetition> getUnFinishedWithPlanDateInRange(Date from, Date to) {
        return this.entityManager.unwrap(Session.class)
                .createQuery("from Repetition where planDate >= :from and planDate <= :to and factDate is null order by planDate asc")
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public long numberOfRepetitionsInRange(Date from, Date to, boolean isRepOnly) {
        return (long) this.entityManager.unwrap(Session.class)
                .createQuery("select count(*) from Repetition where planDate >= :from and planDate <= :to and factDate is null and isRepetitionOnly is :isRepOnly")
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("isRepOnly", isRepOnly)
                .uniqueResult();
    }

    @Override
    public void makeRepOnlyAllUnfinished(SpacedRepetitions spacedRepetition) {
        List<Repetition> repetitions = this.entityManager.unwrap(Session.class).createQuery("from Repetition where spacedRepetitions.id = :sp and factDate is null")
                .setParameter("sp", spacedRepetition.getId()).list();
        repetitions.forEach(rep->{
            rep.setIsRepetitionOnly(true);
            save(rep);
        });
    }

    @Override
    public void delete(Repetition repetition) {
        this.entityManager.unwrap(Session.class).delete(repetition);
    }
}
