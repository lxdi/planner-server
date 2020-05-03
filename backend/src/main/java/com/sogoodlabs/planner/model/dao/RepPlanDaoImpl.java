package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;


@Service
@Transactional
public class RepPlanDaoImpl implements IRepPlanDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(RepetitionPlan repetitionPlan) {
        entityManager.unwrap(Session.class).saveOrUpdate(repetitionPlan);
    }

    @Override
    public RepetitionPlan getById(long id) {
        return entityManager.unwrap(Session.class).get(RepetitionPlan.class, id);
    }

    @Override
    public RepetitionPlan getByTitle(String title) {
        return (RepetitionPlan) entityManager.unwrap(Session.class)
                .createQuery("from RepetitionPlan where title=:title")
                .setParameter("title", title)
                .uniqueResult();
    }

    @Override
    public List<RepetitionPlan> getAll() {
        return entityManager.unwrap(Session.class).createQuery("from RepetitionPlan").getResultList();
    }


}
