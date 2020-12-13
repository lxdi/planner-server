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
    List<Repetition> findByPlanDay(Day planDay);

    @Query("from Repetition where planDay in :days")
    List<Repetition> findByPlanDays(@Param("days") List<Day> planDay);

    @Query("select count(*) from Repetition where planDay = :day")
    int findTotalByPlanDay(@Param("day") Day day);

    @Query("select count(*) from Repetition where planDay = :day and factDay is null")
    int findTotalByPlanDayUnfinished(@Param("day") Day day);

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
