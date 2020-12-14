package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.controllers.dto.ActualActivityDto;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActualActivityService {

    private static final String URGENCY_UPCOMING = "upcoming";
    private static final String URGENCY_ABOUT_WEEK = "about week";
    private static final String URGENCY_WEEK_LATE = "1 week late";
    private static final String URGENCY_2_WEEKS_LATE = "2 weeks late";

    private static final int URGENCY_ABOUT_WEEK_RANGE_IN_DAYS = 3;
    private static final int URGENCY_UPCOMING_IN_DAYS = 10;
    private static final int URGENCY_WEEK_LATE_IN_DAYS = 10;
    private static final int URGENCY_2_WEEKS_LATE_IN_DAYS = 17;

    @Autowired
    public IRepDAO repDAO;

    @Autowired
    public IDayDao dayDao;


    public ActualActivityDto getActualActivity(){
        ActualActivityDto actualActivityDto = new ActualActivityDto();

        Date currentDate = DateUtils.currentDate();


        List<Day> days = dayDao.findInRange(DateUtils.subtractDays(currentDate, URGENCY_2_WEEKS_LATE_IN_DAYS),
                DateUtils.addDays(currentDate, URGENCY_UPCOMING_IN_DAYS)).stream()
                .peek(day -> day.setUrgency(getUrgencyForDay(day)))
                .collect(Collectors.toList());

        Map<String, List<Repetition>> reps = repDAO.findByPlanDaysUnfinished(days).stream()
                .peek(rep -> rep.getPlanDay().setUrgency(getUrgencyForDay(rep.getPlanDay())))
                .collect(Collectors.groupingBy(rep -> rep.getPlanDay().getUrgency()));

        actualActivityDto.setUpcomingReps(reps.getOrDefault(URGENCY_UPCOMING, new ArrayList<>()));
        actualActivityDto.setAboutWeekReps(reps.getOrDefault(URGENCY_ABOUT_WEEK, new ArrayList<>()));
        actualActivityDto.setOneWeekLate(reps.getOrDefault(URGENCY_WEEK_LATE, new ArrayList<>()));
        actualActivityDto.setTwoWeeksLate(reps.getOrDefault(URGENCY_2_WEEKS_LATE, new ArrayList<>()));

        return actualActivityDto;
    }

    public String getUrgencyForDay(Day day){
        Date currentDate = DateUtils.currentDate();

        if(currentDate.compareTo(day.getDate())==0){
            return URGENCY_ABOUT_WEEK;
        }

        int diff = DateUtils.differenceInDays(currentDate, day.getDate());

        if(diff<0){
            diff = diff*(-1);
        }

        if(diff <= URGENCY_ABOUT_WEEK_RANGE_IN_DAYS){
            return URGENCY_ABOUT_WEEK;
        }

        if(currentDate.before(day.getDate()) && diff <= URGENCY_UPCOMING_IN_DAYS){
            return URGENCY_UPCOMING;
        }

        if(currentDate.after(day.getDate())){
            if(diff <= URGENCY_WEEK_LATE_IN_DAYS){
                return URGENCY_WEEK_LATE;
            }
            if(diff <= URGENCY_2_WEEKS_LATE_IN_DAYS){
                return URGENCY_2_WEEKS_LATE;
            }
        }

        return null;
    }



}
