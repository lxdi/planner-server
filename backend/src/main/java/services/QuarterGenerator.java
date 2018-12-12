package services;

import model.dao.IHQuarterDAO;
import model.dao.IWeekDAO;
import model.entities.HQuarter;
import model.entities.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.List;

import static services.DateUtils.toDate;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
public class QuarterGenerator {

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
        weeksGenerator.generateYear(year);
        System.out.println("Generating quarters for " + year);
        YearWeek start = YearWeek.of(year,1);
        int numberOfWeeks = start.is53WeekYear()? 53: 54;
        YearWeek yw = start ;
        for(int i = 1; i < numberOfWeeks; i++){
            if(i==1 || i == (1+12) || i == (1+12*2) || i==(1+12*3)) {
                HQuarter HQuarter = new HQuarter();
                Date date = toDate(year, yw.atDay(DayOfWeek.MONDAY).getMonthValue(), yw.atDay(DayOfWeek.MONDAY).getDayOfMonth());
                Week startWeek = weekDAO.weekByStartDate(date);
                HQuarter.setStartWeek(startWeek);
                quartalDAO.saveOrUpdate(HQuarter);

                String message = "HQuarter: " + yw + " | start: " + yw.atDay(DayOfWeek.MONDAY);
                System.out.println(message);
            }
            // Prepare for next loop.
            yw = yw.plusWeeks(1);
        }
    }

}
