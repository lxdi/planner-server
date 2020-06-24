package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */

@Transactional
public interface ITargetsDAO extends JpaRepository<Target, Long> {

    @Query("from Target where realm = :realm and parent = :parent and next is null")
    Target findLast(@Param("parent") Target parent, @Param("realm") Realm realm);

    @Query("from Target where realm = :realm and parent is null and next is null")
    Target findLastAmongRoots(@Param("realm") Realm realm);

}
