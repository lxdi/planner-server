package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Week;
import com.sogoodlabs.planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeekService {

    private static final int CURRENT_UP_TO_PREV_WEEKS = 4;
    private static final int CURRENT_UP_TO_NEXT_WEEKS = 30;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private IWeekDAO weekDAO;

    @Autowired
    private WeeksGenerator weeksGenerator;

    public void fill(Week week){
        week.setDays(new ArrayList<>());
        dayDao.findByWeek(week).forEach(week.getDays()::add);
    }


    public List<Week> getCurrent(){
        int year = DateUtils.getYear(DateUtils.currentDate());

        Day today = dayDao.findByDate(DateUtils.currentDate());

        if(today==null){
            weeksGenerator.generateYear(year);
            today = dayDao.findByDate(DateUtils.currentDate());
        }

        Week currentWeek = today.getWeek();

        List<Week> weeks = new ArrayList<>();

        if(currentWeek.getNum()<=CURRENT_UP_TO_PREV_WEEKS){
            Week lastWeekPrevYear = weekDAO.findLastInYear(year-1);

            if(lastWeekPrevYear==null){
                weeksGenerator.generateYear(year);
                lastWeekPrevYear = weekDAO.findLastInYear(year-1);
            }

            int difference = currentWeek.getNum()-CURRENT_UP_TO_PREV_WEEKS;
            if(difference-1 > 0){
                weeks.addAll(weekDAO.findInDiapason(
                        lastWeekPrevYear.getNum()-difference, lastWeekPrevYear.getNum()-1, year-1));
            }
            weeks.add(lastWeekPrevYear);
        }

        weeks.addAll(weekDAO.findInDiapason(
                currentWeek.getNum()-CURRENT_UP_TO_PREV_WEEKS, currentWeek.getNum()-1, year));

        weeks.add(currentWeek);

        Week lastWeekCurrentYear = weekDAO.findLastInYear(year);

        if(lastWeekCurrentYear.getNum()<currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS){
            weeks.addAll(weekDAO.findInDiapason(currentWeek.getNum()+1, lastWeekCurrentYear.getNum(), year));

            List<Week> nextYearWeeks = weekDAO.findInDiapason(
                    0, currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS-lastWeekCurrentYear.getNum(), year+1);

            if(nextYearWeeks == null || nextYearWeeks.size()==0){
                weeksGenerator.generateYear(year+1);

                nextYearWeeks = weekDAO.findInDiapason(
                        0, currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS-lastWeekCurrentYear.getNum(), year+1);
            }

            weeks.addAll(nextYearWeeks);
        } else {
            weeks.addAll(weekDAO.findInDiapason(currentWeek.getNum()+1, currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS, year));
        }

        return weeks;
    }

}
