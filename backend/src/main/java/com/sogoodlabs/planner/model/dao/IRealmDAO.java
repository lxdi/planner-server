package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IRealmDAO extends JpaRepository<Realm, String> {

    @Query("update Realm set current = false")
    void clearCurrent();

}
