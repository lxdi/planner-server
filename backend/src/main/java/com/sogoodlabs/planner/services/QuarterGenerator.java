package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.List;

import static com.sogoodlabs.planner.services.DateUtils.fromDate;
import static com.sogoodlabs.planner.services.DateUtils.toDate;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
public class QuarterGenerator {

    private static final int HQUARTERS_PER_YEAR = 12;
    private static final int HQUARTERS_DURATION = 4;

    @Autowired
    IHQuarterDAO quartalDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    WeeksGenerator weeksGenerator;


    public void generate(List<Integer> years){
        for(int year : years){
            generateYear(year);
        }
    }

    public void generateYear(int year){
        assert quartalDAO.getHQuartersInYear(year).size()==0;
        weeksGenerator.generateYear(year);
        System.out.println("Generating quarters for " + year);
        YearWeek start = YearWeek.of(year,1);
        int numberOfWeeks = start.is53WeekYear()? 53: 54;
        YearWeek yw = start;
        for(int i = 1; i <= HQUARTERS_PER_YEAR; i++){
                if(yw.getWeek()==1 && yw.atDay(DayOfWeek.MONDAY).getMonthValue()==12){
                    yw = yw.plusWeeks(1); //if the first week starts in the last year then start from the second week
                }
                Date startDate = toDate(year, yw.atDay(DayOfWeek.MONDAY).getMonthValue(), yw.atDay(DayOfWeek.MONDAY).getDayOfMonth());
                Week startWeek = weekDAO.weekByStartDate(startDate);

                yw = yw.plusWeeks(HQUARTERS_DURATION-1);
                Date endDate = toDate(year, yw.atDay(DayOfWeek.MONDAY).getMonthValue(), yw.atDay(DayOfWeek.MONDAY).getDayOfMonth());
                Week endWeek = weekDAO.weekByStartDate(endDate);

                HQuarter HQuarter = new HQuarter(startWeek, endWeek);
                quartalDAO.saveOrUpdate(HQuarter);
                yw = yw.plusWeeks(1);
                //hquartersPerYearLimit--;

                String message = "HQuarter: " + HQuarter.getStartWeek().getNumber() + " | start: " + fromDate(HQuarter.getStartWeek().getStartDay());
                System.out.println(message);
        }
    }

}
