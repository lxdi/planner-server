package test_configs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCreatorsAnotherSession extends ATestCreators {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void save(Object object) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(object);
        session.getTransaction().commit();
        session.close();
    }
}
