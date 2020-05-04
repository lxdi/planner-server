package com.sogoodlabs.planner.test_configs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;

@Service
public class TestCreatorsAnotherSession extends ATestCreators {

    @Autowired
    EntityManagerFactory etf;

    @Override
    public void save(Object object) {
        SessionFactory sessionFactory = etf.unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(object);
        session.getTransaction().commit();
        session.close();
    }
}
