package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class RealmDao implements IRealmDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Realm> getAllRealms() {
        return entityManager.unwrap(Session.class).createQuery("from Realm").list();
        //return entityManager.unwrap(Session.class).createCriteria(Realm.class).list();
    }

    @Override
    public Realm realmById(long id) {
        return entityManager.unwrap(Session.class).get(Realm.class, id);
    }

    @Override
    public Realm createRealm(String title) {
        Realm realm = new Realm(title);
        this.saveOrUpdate(realm);
        return realm;
    }

    @Override
    public void saveOrUpdate(Realm realm) {
        entityManager.unwrap(Session.class).saveOrUpdate(realm);
    }

    @Override
    public void setCurrent(long realmid) {
        this.entityManager.unwrap(Session.class).createQuery("update Realm set current=false").executeUpdate();
        this.entityManager.unwrap(Session.class).createQuery("update Realm set current=true where id = :id")
                .setParameter("id", realmid)
                .executeUpdate();
    }
}
