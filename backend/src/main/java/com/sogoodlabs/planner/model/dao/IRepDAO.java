package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Transactional
public interface IRepDAO extends JpaRepository<Repetition, String> {

    List<Repetition> findByTask(Task task);

    @Query("from Repetition where task = :task and repetitionPlan.dayStep is false and factDay is null")
    List<Repetition> findByTaskActive(@Param("task") Task task);

    @Query("from Repetition where planDay = :planDay and repetitionPlan.dayStep is false")
    List<Repetition> findByPlanDay(@Param("planDay") Day planDay);

    @Query("from Repetition where (planDay = :day or factDay = :day) and repetitionPlan.dayStep is false")
    List<Repetition> findByPlanDayOrFactDay(@Param("day") Day day);

    @Query("from Repetition where planDay in :days and factDay is null and repetitionPlan.dayStep is false")
    List<Repetition> findByPlanDaysUnfinished(@Param("days") List<Day> planDays);

    @Query("from Repetition where planDay in :days and factDay is null and repetitionPlan.dayStep is true")
    List<Repetition> findByPlanDaysUnfinishedMemo(@Param("days") List<Day> planDays);

    @Query("select count(*) from Repetition where planDay = :day and repetitionPlan.dayStep is false")
    int findTotalByPlanDay(@Param("day") Day day);

    @Query("select count(*) from Repetition where factDay = :day and repetitionPlan.dayStep is false")
    int findTotalByFactDay(@Param("day") Day day);

    @Query("select count(*) from Repetition where planDay = :day and factDay is null and repetitionPlan.dayStep is false")
    int findTotalByPlanDayUnfinished(@Param("day") Day day);

    @Query("select count(*) from Repetition where factDay.date >= :date")
    int findTotalFinishedAfter(@Param("date") Date date);

//    @Query("from Repetition where spacedRepetitions.id = :srId")
//    List<Repetition> getRepsbySpacedRepId(@Param("srId") long srId);
//
//    @Query("from Repetition where planDate >= :from and planDate <= :to and factDate is null order by planDate asc")
//    List<Repetition> getUnFinishedWithPlanDateInRange(@Param("from") Date from, @Param("to") Date to);
//
//    @Query("select count(*) from Repetition where planDate >= :from and planDate <= :to and factDate is null and isRepetitionOnly is :isRepOnly")
//    long numberOfRepetitionsInRange(@Param("from") Date from, @Param("to") Date to, @Param("isRepOnly") boolean isRepOnly);
//
//    @Query("select count(*) from Repetition where spacedRepetitions.repetitionPlan.dayStep is false " +
//            "and planDate >= :from and planDate <= :to and factDate is null " +
//            "and isRepetitionOnly is :isRepOnly")
//    long numberOfRepetitionsInRangeMonthStep(@Param("from") Date from, @Param("to") Date to, @Param("isRepOnly") boolean isRepOnly);

}
