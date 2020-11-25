package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */

@Transactional
public interface ITargetsDAO extends JpaRepository<Target, String> {


    @Query("from Target t where t.parent = :target")
    List<Target> getChildren(@Param("target") Target target);

    @Query("from Target t where t.title = :title")
    Target getTargetByTitle(@Param("title") String title);

    @Query("FROM Target where next = :next")
    Target getPrevTarget(@Param("next") Target target);

    @Query("FROM Target where realm = :realm and next is null and parent = :parent")
    Target getLastOfChildren(@Param("parent") Target targetParent, @Param("realm") Realm realm);

    @Query("FROM Target where realm = :realm and next is null and parent is null")
    Target getLastOfChildrenRoot(@Param("realm")Realm realm);

    @Query("select count(*)=0 from Target where parent = :target")
    boolean isLeafTarget(@Param("target") Target target);

}
