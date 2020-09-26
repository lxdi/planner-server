package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.Week;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.List;

import static com.sogoodlabs.planner.util.DateUtils.addDays;
import static com.sogoodlabs.planner.util.DateUtils.toDate;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
public class WeeksGenerator {

    Logger log = LoggerFactory.getLogger(WeeksGenerator.class);

    @Autowired
    IWeekDAO weekDAO;

    public void generate(List<Integer> years){
        for(int year : years){
            generateYear(year);
        }
    }

    public void generateYear(int year){
        log.info("Generating weeks for " + year);
        YearWeek start = YearWeek.of(year,1);
        int numberOfWeeks = start.is53WeekYear()? 53: 54;
        YearWeek yw = start ;
        for(int i = 1; i < numberOfWeeks; i++){
            Date startDate = toDate(yw.atDay(DayOfWeek.MONDAY).getYear(), yw.atDay(DayOfWeek.MONDAY).getMonthValue(), yw.atDay(DayOfWeek.MONDAY).getDayOfMonth());
            //Date endDate = toDate(year, yw.atDay( DayOfWeek.SUNDAY ).getMonthValue(), yw.atDay( DayOfWeek.SUNDAY ).getDayOfMonth());
            Date endDate = addDays(startDate, 6);
            String message = "Week: " + yw + " | start: " + yw.atDay( DayOfWeek.MONDAY ) + " | stop: " + yw.atDay( DayOfWeek.SUNDAY ) ;
            log.info(message);
            getOrCreateWeek(startDate, endDate, i);
            // Prepare for next loop.
            yw = yw.plusWeeks( 1 ) ;

        }
    }

    private Week getOrCreateWeek(Date startDate, Date endDate, int number){
        Week week = weekDAO.weekByStartDate(startDate);
        if(week==null){
            week = new Week(startDate, endDate, number);
            weekDAO.saveOrUpdate(week);
        }
        return week;
    }

}
