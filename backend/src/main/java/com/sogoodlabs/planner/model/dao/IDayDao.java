package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.DaysOfWeek;
import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Transactional
public interface IDayDao extends JpaRepository<Day, Long> {

    @Query("from Day where date = :date")
    Day byDate(@Param("date") Date date);
    
    @Query("from Day where date >= :#{#week.startDay.date} and date <= :#{#week.endDay.date} and dayOfWeek = :dayOfWeek")
    Day byWeekAndDayOfWeek(@Param("week") Week week, @Param("dayOfWeek") DaysOfWeek dayOfWeek);
}
