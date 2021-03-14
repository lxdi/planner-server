package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IRealmDAO extends JpaRepository<Realm, String> {

    @Modifying
    @Query("update Realm r set current = false where r != :curRealm ")
    void clearCurrent(@Param("curRealm") Realm curRealm);

}
