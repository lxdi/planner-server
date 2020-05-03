package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.SpacedRepetitions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

@Transactional
@Service
public class SpacedRepDAOimpl implements ISpacedRepDAO {
    
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void save(SpacedRepetitions spacedRepetitions){
        this.entityManager.unwrap(Session.class).saveOrUpdate(spacedRepetitions);
    }

    @Override
    public SpacedRepetitions getSRforTaskMapper(long tmId) {
        return (SpacedRepetitions) entityManager.unwrap(Session.class)
                .createQuery("from SpacedRepetitions where taskMapper.id = :tmId")
                .setParameter("tmId", tmId)
                .uniqueResult();
    }

    @Override
    public SpacedRepetitions getSRforTask(long taskid) {
        return (SpacedRepetitions) entityManager.unwrap(Session.class)
                .createQuery("from SpacedRepetitions where taskMapper.task.id = :taskid")
                .setParameter("taskid", taskid)
                .uniqueResult();
    }
}
