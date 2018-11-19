package services;

import model.IQuarterDAO;
import model.entities.Quarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
public class QuarterGenerator {

    @Autowired
    IQuarterDAO quartalDAO;

    public void generate(List<Integer> years){
        for(int year : years){
            generateYear(year);
        }
    }

    public void generateYear(int year){
        System.out.println("Generating quarters for " + year);
        YearWeek start = YearWeek.of(year,1);
        int numberOfWeeks = start.is53WeekYear()? 53: 54;
        YearWeek yw = start ;
        for(int i = 1; i < numberOfWeeks; i++){
            if(i==1 || i == (1+12) || i == (1+12*2) || i==(1+12*3)) {
                Quarter quarter = new Quarter();
                quarter.setYear(year);
                quarter.setStartDay(yw.atDay(DayOfWeek.MONDAY).getDayOfMonth());
                quarter.setStartMonth(yw.atDay(DayOfWeek.MONDAY).getMonthValue());
                quartalDAO.saveOrUpdate(quarter);

                String message = "Quarter: " + yw + " | start: " + yw.atDay(DayOfWeek.MONDAY);
                System.out.println(message);
            }
            // Prepare for next loop.
            yw = yw.plusWeeks(1);
        }
    }

}
