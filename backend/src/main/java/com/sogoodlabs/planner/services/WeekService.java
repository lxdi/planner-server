package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dto.MovingPlansDto;
import com.sogoodlabs.planner.model.dto.ScheduledDayDto;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.TaskMapper;
import com.sogoodlabs.planner.model.entities.Week;
import com.sogoodlabs.planner.util.DateUtils;
import com.sogoodlabs.planner.util.HibernateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeekService {

    Logger log = LoggerFactory.getLogger(WeekService.class);

    protected static final int CURRENT_UP_TO_PREV_WEEKS = 4;
    protected static final int CURRENT_UP_TO_NEXT_WEEKS = 30;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private IWeekDAO weekDAO;

    @Autowired
    private WeeksGenerator weeksGenerator;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ActualActivityService actualActivityService;

    @Autowired
    private IExternalTaskDao externalTaskDao;

    @Autowired
    private MoveRepetitionsService moveRepetitionsService;

    public Week fill(Week weekProxy){
        Week week = HibernateUtils.initializeAndUnproxy(weekProxy);
        week.setDays(new ArrayList<>());

        dayDao.findByWeek(week).stream()
                .peek(this::fillDay)
                .forEach(week.getDays()::add);

        return week;
    }

    public void fillDay(Day day){
        day.setMappersNum(taskMappersDAO.findTotalByDay(day));
        day.setMappersNumUnfinished(taskMappersDAO.findTotalByPlanDayUnfinished(day));

        day.setRepsNum(repDAO.findTotalByFactDay(day));
        day.setRepsNumUnfinished(repDAO.findTotalByPlanDayUnfinished(day));

        day.setUrgency(actualActivityService.getUrgencyForDay(day));

        day.setExternalTasksNum(externalTaskDao.findTotalByPlanDayUnfinished(day));

    }

    public List<Week> getCurrent(){
        return getWeeksOnDate(DateUtils.currentDate());
    }

    public List<Week> getWeeksOnDate(Date date){
        int year = DateUtils.getYear(date);
        Day today = dayDao.findByDate(date);

        if(today==null){
            weeksGenerator.generateYear(year);
            today = dayDao.findByDate(date);
        }

        Week currentWeek = today.getWeek();
        List<Week> weeks = new ArrayList<>();

        if(currentWeek.getNum()<=CURRENT_UP_TO_PREV_WEEKS){
            fillPrevYear(weeks, year, currentWeek);
        }

        weeks.addAll(weekDAO.findInDiapason(
                currentWeek.getNum()-CURRENT_UP_TO_PREV_WEEKS, currentWeek.getNum()-1, year));

        weeks.add(currentWeek);
        Week lastWeekCurrentYear = weekDAO.findLastInYear(year);

        if(lastWeekCurrentYear.getNum()<currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS){
            fillNextYear(weeks, currentWeek, lastWeekCurrentYear, year);
        } else {
            weeks.addAll(weekDAO.findInDiapason(currentWeek.getNum()+1, currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS, year));
        }

        return weeks;
    }

    private void fillPrevYear(List<Week> weeks, int year, Week currentWeek){
        Week lastWeekPrevYear = weekDAO.findLastInYear(year-1);

        if(lastWeekPrevYear==null){
            weeksGenerator.generateYear(year-1);
            lastWeekPrevYear = weekDAO.findLastInYear(year-1);
        }

        int difference = Math.abs(currentWeek.getNum()-CURRENT_UP_TO_PREV_WEEKS);

        if(difference-1 > 0){
            weeks.addAll(weekDAO.findInDiapason(
                    lastWeekPrevYear.getNum()-difference, lastWeekPrevYear.getNum()-1, year-1));
        }

        weeks.add(lastWeekPrevYear);
    }

    private void fillNextYear(List<Week> weeks, Week currentWeek, Week lastWeekCurrentYear, int year){
        weeks.addAll(weekDAO.findInDiapason(currentWeek.getNum()+1, lastWeekCurrentYear.getNum(), year));

        List<Week> nextYearWeeks = weekDAO.findInDiapason(
                0, currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS-lastWeekCurrentYear.getNum(), year+1);

        if(nextYearWeeks == null || nextYearWeeks.size()==0){
            weeksGenerator.generateYear(year+1);

            nextYearWeeks = weekDAO.findInDiapason(
                    0, currentWeek.getNum()+CURRENT_UP_TO_NEXT_WEEKS-lastWeekCurrentYear.getNum(), year+1);
        }

        weeks.addAll(nextYearWeeks);
    }

    public Week getPrev(String currentId){
        Week currentWeek = weekDAO.findById(currentId).orElseThrow(() -> new RuntimeException("No week found by "+currentId));

        if(currentWeek.getPrev()==null){
            weeksGenerator.generateYear(DateUtils.getYear(DateUtils.currentDate())-1);
            currentWeek = weekDAO.findById(currentId).orElseThrow(() -> new RuntimeException("No week found by "+currentId));
        }

        return currentWeek.getPrev();
    }

    public Week getNext(String currentId){
        Week currentWeek = weekDAO.findById(currentId).orElseThrow(() -> new RuntimeException("No week found by "+currentId));

        if(currentWeek.getNext()==null){
            weeksGenerator.generateYear(DateUtils.getYear(DateUtils.currentDate())+1);
            currentWeek = weekDAO.findById(currentId).orElseThrow(() -> new RuntimeException("No week found by "+currentId));
        }

        return currentWeek.getNext();
    }

    public ScheduledDayDto getScheduledDayDto(Day day){
        ScheduledDayDto dto = new ScheduledDayDto();
        dto.setTaskMappers(taskMappersDAO.findByPlanDayOrFinishDay(day, day));
        dto.setRepetitions(repDAO.findByPlanDayOrFactDay(day));
        dto.setExternalTasks(externalTaskDao.findByDay(day));
        return dto;
    }

    public void movePlans(MovingPlansDto movingPlansDto){
        Day day = dayDao.findById(movingPlansDto.getTargetDayId())
                .orElseThrow(() -> new RuntimeException("Day no found " + movingPlansDto.getTargetDayId()));

        if(movingPlansDto.getTaskMappersIds()!=null){
            movingPlansDto.getTaskMappersIds().forEach(taskMapperId -> {
                TaskMapper taskMapper = taskMappersDAO.findById(taskMapperId).orElseThrow(() -> new RuntimeException("TaskMapper not found " + taskMapperId));
                taskMapper.setPlanDay(day);
                taskMappersDAO.save(taskMapper);
                log.info("Moving task {} to {}", taskMapper.getTask().getTitle(), DateUtils.fromDate(day.getDate()));
            });
        }

        if(movingPlansDto.getRepetitionIds()!=null){
            moveRepetitionsService.move(movingPlansDto.getRepetitionIds().stream()
                    .map(repId -> repDAO.findById(repId).orElseThrow(() -> new RuntimeException("Repetition not found " + repId)))
                    .collect(Collectors.toList()), day);
        }
    }

}
