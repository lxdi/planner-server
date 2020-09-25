package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.SpacedRepetitions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

@Transactional
public interface ISpacedRepDAO extends JpaRepository<SpacedRepetitions, Long> {

    @Query("from SpacedRepetitions where taskMapper.id = :tmId")
    SpacedRepetitions getSRforTaskMapper(@Param("tmId") long tmId);

    @Query("from SpacedRepetitions where taskMapper.task.id = :taskid")
    SpacedRepetitions getSRforTask(@Param("taskid") long taskid);

}
