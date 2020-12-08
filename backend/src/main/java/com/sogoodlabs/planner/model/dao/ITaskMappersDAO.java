package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface ITaskMappersDAO extends JpaRepository<TaskMapper, String> {

    List<TaskMapper> findByTask(Task task);
    List<TaskMapper> findByPlanDay(Day planDay);

    @Query("select count(*) from TaskMapper where planDay = :day")
    int findTotalByPlanDay(@Param("day") Day day);

    @Query("select count(*) from TaskMapper where planDay = :day and finishDay is null")
    int findTotalByPlanDayUnfinished(@Param("day") Day day);

}
