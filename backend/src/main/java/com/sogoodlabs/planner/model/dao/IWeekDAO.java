package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 * Created by Alexander on 22.04.2018.
 */

@Transactional
public interface IWeekDAO extends JpaRepository<Week, String> {

    Week findByYearAndNum(int year, int num);
    List<Week> findByYear(int year);

    @Query(value = "select * from Week where year = :year order by num desc limit 1", nativeQuery = true)
    Week findLastInYear(@Param("year") int year);

    @Query(value = "select * from Week where year = :year order by num asc limit 1", nativeQuery = true)
    Week findFirsInYear(@Param("year") int year);

}
