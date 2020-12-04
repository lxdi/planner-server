package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.DaysOfWeek;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static com.sogoodlabs.planner.util.DateUtils.toDate;

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
        int dayCount = 1;
        Map<Integer, Week> weeksMap = new TreeMap<>();
        for(int i = 1; i < numberOfWeeks; i++){
            Week week = getOrCreateWeek(year, i);
            weeksMap.put(i, week);
            getOrCreateDay(getDate(yw, DayOfWeek.MONDAY), week, dayCount++, DaysOfWeek.of(DayOfWeek.MONDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.TUESDAY), week, dayCount++, DaysOfWeek.of(DayOfWeek.TUESDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.WEDNESDAY), week, dayCount++, DaysOfWeek.of(DayOfWeek.WEDNESDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.THURSDAY), week, dayCount++, DaysOfWeek.of(DayOfWeek.THURSDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.FRIDAY), week, dayCount++, DaysOfWeek.of(DayOfWeek.FRIDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.SATURDAY), week, dayCount++, DaysOfWeek.of(DayOfWeek.SATURDAY));
            getOrCreateDay(getDate(yw, DayOfWeek.SUNDAY), week, dayCount++, DaysOfWeek.of(DayOfWeek.SUNDAY));

            log.info("Week: {} | start: {} | stop: {}", yw, yw.atDay( DayOfWeek.MONDAY ), yw.atDay(DayOfWeek.SUNDAY));
            // Prepare for next loop.
            yw = yw.plusWeeks( 1 ) ;
        }

        weeksMap.values().forEach(week -> {
            if(week.getNum()>1){
                week.setPrev(weeksMap.get(week.getNum()-1));
            } else {
                Week lastInPrevYear = weekDAO.findLastInYear(year-1);
                if(lastInPrevYear!=null){
                    week.setPrev(lastInPrevYear);
                    lastInPrevYear.setNext(week);
                    weekDAO.save(lastInPrevYear);
                }
            }

            if(week.getNum()<numberOfWeeks-1){
                week.setNext(weeksMap.get(week.getNum()+1));
            } else {
                Week firstInNextYear = weekDAO.findFirstInYear(year+1);
                if(firstInNextYear!=null){
                    week.setNext(weekDAO.findFirstInYear(year+1));
                    firstInNextYear.setPrev(week);
                    weekDAO.save(firstInNextYear);
                }
            }

            weekDAO.save(week);
        });

    }

    private Week getOrCreateWeek(int year, int number){
        Week week = weekDAO.findByYearAndNum(year, number);
        if(week==null){
            week = new Week();
            week.setId(UUID.randomUUID().toString());
            week.setNum(number);
            week.setYear(year);
            weekDAO.save(week);
        }
        return week;
    }

    private Date getDate(YearWeek yw, DayOfWeek dayOfWeek){
        LocalDate localDate = yw.atDay(dayOfWeek);
        return toDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    private Day getOrCreateDay(Date date, Week week, int num, DaysOfWeek dayOfWeek){
        Day day = dayDao.findByDate(date);
        if(day==null){
            day = new Day();
            day.setId(UUID.randomUUID().toString());
            day.setDate(date);
            day.setWeek(week);
            day.setNum(num);
            day.setWeekDay(dayOfWeek);
            //TODO fill day of week
            dayDao.save(day);
        }
        return day;
    }

}
