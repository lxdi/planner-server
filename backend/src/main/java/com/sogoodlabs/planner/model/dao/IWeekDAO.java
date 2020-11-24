package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 * Created by Alexander on 22.04.2018.
 */

@Transactional
public interface IWeekDAO extends JpaRepository<Week, String> {

    Week findByYearByNum(int year, int num);

}
