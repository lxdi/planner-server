package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Week;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.sogoodlabs.planner.services.DateUtils.addDays;
import static com.sogoodlabs.planner.services.DateUtils.toDate;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
public class WeeksGenerator {

    Logger log = LoggerFactory.getLogger(WeeksGenerator.class);

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    IDayDao dayDao;

    public void generate(List<Integer> years){
        for(int year : years){
            generateYear(year);
        }
    }

    public void generateYear(int year){
        log.info("Generating weeks for {}", year);
        YearWeek yw = YearWeek.of(year,1);
        int numberOfWeeks = yw.is53WeekYear()? 53: 54;
        for(int i = 1; i < numberOfWeeks; i++){
            Day startDay = getOrCreateDay(getDate(yw, DayOfWeek.MONDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.TUESDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.WEDNESDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.THURSDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.FRIDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.SATURDAY));
            Day endDay = getOrCreateDay(getDate(yw, DayOfWeek.SUNDAY));
            log.info("Week: {} | start: {} | stop: {}", yw, yw.atDay( DayOfWeek.MONDAY ), yw.atDay(DayOfWeek.SUNDAY));
            getOrCreateWeek(startDay, endDay, i);
            // Prepare for next loop.
            yw = yw.plusWeeks( 1 ) ;
        }
    }

    private Week getOrCreateWeek(Day startDay, Day endDay, int number){
        Week week = weekDAO.weekByStartDay(startDay);
        if(week==null){
            week = new Week(startDay, endDay, number);
            weekDAO.saveOrUpdate(week);
        }
        return week;
    }

    private Date getDate(YearWeek yw, DayOfWeek dayOfWeek){
        LocalDate localDate = yw.atDay(dayOfWeek);
        return toDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    private Day getOrCreateDay(Date date){
        Day day = dayDao.byDate(date);
        if(day==null){
            day = new Day();
            day.setDate(date);
            //TODO fill day of week
            dayDao.save(day);
        }
        return day;
    }

}
