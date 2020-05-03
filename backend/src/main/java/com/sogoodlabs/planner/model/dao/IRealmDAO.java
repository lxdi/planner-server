package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;

import java.util.List;

public interface IRealmDAO {

    List<Realm> getAllRealms();
    Realm realmById(long id);
    Realm createRealm(String title);
    void saveOrUpdate(Realm realm);
    void setCurrent(long realmid);
}
