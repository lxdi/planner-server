package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */

@Transactional
public interface IMeansDAO extends JpaRepository<Mean, Long> {

    @Query("FROM Mean mean where mean.parent = :parent")
    List<Mean> getChildren(@Param("parent") Mean mean);

    @Query("FROM Mean mean where mean.title = :title")
    Mean meanByTitle(@Param("title") String title);

    @Query("FROM Mean mean where mean.next = :next")
    Mean getPrevMean(@Param("next") Mean mean);

    @Query("FROM Mean where realm = :realm and next is null and parent = :parent")
    Mean getLastOfChildren(@Param("parent") Mean mean, @Param("realm") Realm realm);

    @Query("FROM Mean where realm = :realm and next is null and parent is null")
    Mean getLastOfChildrenRoot(@Param("realm")Realm realm);

    @Query("select count(*) from Slot s where s.mean = :mean")
    long assignsMeansCount(@Param("mean")Mean mean);

    @Query("from Mean m where :target in elements(targets)")
    List<Mean> meansAssignedToTarget(@Param("target") Target target);



}
