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
    List<TaskMapper> findByPlanDayOrFinishDay(Day planDay, Day planDay2);

    @Query("""
    from TaskMapper 
        where (finishDay is null and planDay = :planDay)
        or finishDay = :finishDay
    """)
    List<TaskMapper> findByEitherDay(Day planDay, Day finishDay);

    @Query("from TaskMapper where task = :task and finishDay is null")
    List<TaskMapper> findByTaskUnfinished(@Param("task") Task task);

    @Query("from TaskMapper where task = :task and finishDay is not null")
    List<TaskMapper> findByTaskFinished(@Param("task") Task task);

    @Query("select count(*) from TaskMapper where finishDay = :day")
    int findTotalByDay(@Param("day") Day day);

    @Query("select count(*) from TaskMapper where planDay = :day and finishDay is null")
    int findTotalByPlanDayUnfinished(@Param("day") Day day);

    @Query("select count(*) from TaskMapper where finishDay.date >= :date")
    int findTotalFinishedAfter(@Param("date") Date date);

}
