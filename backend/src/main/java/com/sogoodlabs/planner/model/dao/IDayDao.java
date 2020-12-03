package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Transactional
public interface IDayDao extends JpaRepository<Day, String> {

    Day findByDate(Date date);
    List<Day> findByWeek(Week week);

}
