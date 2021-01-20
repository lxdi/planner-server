package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.ExternalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface IExternalTaskDao extends JpaRepository<ExternalTask, String> {

    List<ExternalTask> findByDay(Day day);

    @Query("select count(*) from ExternalTask where day = :day")
    int findTotalByPlanDayUnfinished(@Param("day") Day day);

    @Query("select sum(hours) from ExternalTask where day.date >= :date")
    int findHoursFinishedAfter(@Param("date") Date date);

}
