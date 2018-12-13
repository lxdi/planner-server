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

import static services.DateUtils.fromDate;
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
        YearWeek yw = start;
        int hquartersPerYearLimit = 8;
        int hquarterlength = 6;
        for(int i = 1; i <= hquartersPerYearLimit; i++){
            //if(i==1 || (i-1)%6==0 && hquartersPerYearLimit>0){
            //if(i==1 || i == (1+12) || i == (1+12*2) || i==(1+12*3)) {
                if(yw.getWeek()==1 && yw.atDay(DayOfWeek.MONDAY).getMonthValue()==12){
                    yw = yw.plusWeeks(1); //if the first week starts in the last year then start from the second week
                }
                Date startDate = toDate(year, yw.atDay(DayOfWeek.MONDAY).getMonthValue(), yw.atDay(DayOfWeek.MONDAY).getDayOfMonth());
                Week startWeek = weekDAO.weekByStartDate(startDate);

                yw = yw.plusWeeks(hquarterlength-1);
                Date endDate = toDate(year, yw.atDay(DayOfWeek.MONDAY).getMonthValue(), yw.atDay(DayOfWeek.MONDAY).getDayOfMonth());
                Week endWeek = weekDAO.weekByStartDate(endDate);

                HQuarter HQuarter = new HQuarter(startWeek, endWeek);
                quartalDAO.saveOrUpdate(HQuarter);
                yw = yw.plusWeeks(1);
                //hquartersPerYearLimit--;

                String message = "HQuarter: " + HQuarter.getStartWeek().getNumber() + " | start: " + fromDate(HQuarter.getStartWeek().getStartDay());
                System.out.println(message);
            //}
            // Prepare for next loop.
            //yw = yw.plusWeeks(1);
        }
    }

}
