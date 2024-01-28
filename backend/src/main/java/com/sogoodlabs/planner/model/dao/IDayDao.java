package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Transactional
public interface IDayDao extends JpaRepository<Day, String> {

    Day findByDate(Date date);
    List<Day> findByWeek(Week week);

    List<Day> findAllByDate(Date date);

    @Query("from Day where date >= :from and date <= :to")
    List<Day> findInRange(@Param("from") Date from, @Param("to") Date to);

}
