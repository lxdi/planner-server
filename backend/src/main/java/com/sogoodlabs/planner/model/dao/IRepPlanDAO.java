package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IRepPlanDAO extends JpaRepository<RepetitionPlan, Long> {

    //@Query("from RepetitionPlan where title = :title")
    RepetitionPlan findByTitle(String title);

}
