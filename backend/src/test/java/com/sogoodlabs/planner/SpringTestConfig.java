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
    }

    protected boolean isExists(String id, Class clazz){
        try {
            return entityManager.unwrap(Session.class).get(clazz, id) != null;
        } catch (EntityNotFoundException ex){
            return false;
        }
    }

    protected void cleanContext(){
        Session session = entityManager.unwrap(Session.class);
        session.flush();
        session.clear();
    }

}
