package com.sogoodlabs.planner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Sql(scripts = {"/scripts/clean_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
abstract public class SpringTestConfig {

    @PersistenceContext
    EntityManager entityManager;

    @Before
    public void init(){
//        SessionFactory sessionFactory = etf.unwrap(SessionFactory.class);
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//        session.createQuery("delete from Repetition").executeUpdate();
//        session.createQuery("delete from SpacedRepetitions").executeUpdate();
//        session.createQuery("delete from RepetitionPlan").executeUpdate();
//        session.createQuery("delete from MapperExclusion").executeUpdate();
//        session.createQuery("delete from TaskMapper").executeUpdate();
//        session.createQuery("delete from SlotPosition").executeUpdate();
//        session.createQuery("delete from Slot").executeUpdate();
//        session.createQuery("delete from HQuarter").executeUpdate();
//
//        session.createQuery("delete from Topic").executeUpdate();
//        session.createQuery("delete from TaskTesting").executeUpdate();
//        session.createQuery("delete from Task").executeUpdate();
//        session.createQuery("delete from Subject").executeUpdate();
//        session.createQuery("delete from Layer").executeUpdate();
//        session.createQuery("delete from Mean").executeUpdate();
//        session.createQuery("delete from Target").executeUpdate();
//        session.createQuery("delete from Realm").executeUpdate();
//
//        session.createQuery("delete from Week").executeUpdate();
//
//        session.createQuery("DBCC CHECKIDENT ('Target', RESEED, 0)").executeUpdate();
//
//        session.flush();
//        session.getTransaction().commit();
//        session.close();
    }

    protected boolean isExists(long id, Class clazz){
        try {
            return entityManager.unwrap(Session.class).get(clazz, id) != null;
        } catch (EntityNotFoundException ex){
            return false;
        }
    }

}
