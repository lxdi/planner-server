package services;

import model.dao.IWeekDAO;
import model.entities.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
public class WeeksGenerator {

    @Autowired
    IWeekDAO weekDAO;

    public void generate(List<Integer> years){
        for(int year : years){
            generateYear(year);
        }
    }

    public void generateYear(int year){
        System.out.println("Generating weeks for " + year);
        YearWeek start = YearWeek.of(year,1);
        int numberOfWeeks = start.is53WeekYear()? 53: 54;
        YearWeek yw = start ;
        for(int i = 1; i < numberOfWeeks; i++){
            Week week = new Week();
            week.setYear(year);
            week.setStartDay(yw.atDay( DayOfWeek.MONDAY ).getDayOfMonth());
            week.setStartMonth(yw.atDay( DayOfWeek.MONDAY ).getMonthValue());
            week.setEndDay(yw.atDay(DayOfWeek.SUNDAY).getDayOfMonth());
            week.setEndMonth(yw.atDay(DayOfWeek.SUNDAY).getMonthValue());
            weekDAO.createWeek(week);

            String message = "Week: " + yw + " | start: " + yw.atDay( DayOfWeek.MONDAY ) + " | stop: " + yw.atDay( DayOfWeek.SUNDAY ) ;
            // Prepare for next loop.
            yw = yw.plusWeeks( 1 ) ;
            System.out.println(message);
        }
    }

}
