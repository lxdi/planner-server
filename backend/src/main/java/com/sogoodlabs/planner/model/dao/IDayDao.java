package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface IDayDao extends JpaRepository<Day, Long> {

    @Query("from Day where date :date")
    Day byDate(@Param("date") Date date);
}
