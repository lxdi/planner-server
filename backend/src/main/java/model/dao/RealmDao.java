package model.dao;

import model.entities.Realm;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RealmDao implements IRealmDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Realm> getAllRealms() {
        return sessionFactory.getCurrentSession().createQuery("from Realm").list();
        //return sessionFactory.getCurrentSession().createCriteria(Realm.class).list();
    }

    @Override
    public Realm realmById(long id) {
        return sessionFactory.getCurrentSession().get(Realm.class, id);
    }

    @Override
    public Realm createRealm(String title) {
        Realm realm = new Realm(title);
        this.saveOrUpdate(realm);
        return realm;
    }

    @Override
    public void saveOrUpdate(Realm realm) {
        sessionFactory.getCurrentSession().saveOrUpdate(realm);
    }

    @Override
    public void setCurrent(long realmid) {
        this.sessionFactory.getCurrentSession().createQuery("update Realm set current=false").executeUpdate();
        this.sessionFactory.getCurrentSession().createQuery("update Realm set current=true where id = :id")
                .setParameter("id", realmid)
                .executeUpdate();
    }
}
