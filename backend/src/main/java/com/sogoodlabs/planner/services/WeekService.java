package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.controllers.dto.MovingPlansDto;
import com.sogoodlabs.planner.controllers.dto.ScheduledDayDto;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.TaskMapper;
import com.sogoodlabs.planner.model.entities.Week;
import com.sogoodlabs.planner.util.DateUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeekService {

    Logger log = LoggerFactory.getLogger(WeekService.class);

    private static final int CURRENT_UP_TO_PREV_WEEKS = 4;
    private static final int CURRENT_UP_TO_NEXT_WEEKS = 30;

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

    public Week fill(Week weekProxy){
        Week week = initializeAndUnproxy(weekProxy);
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
            movingPlansDto.getRepetitionIds().forEach(repid -> {
                Repetition repetition = repDAO.findById(repid).orElseThrow(() -> new RuntimeException("Repetition not found " + repid));
                repetition.setPlanDay(day);
                repDAO.save(repetition);
                log.info("Moving repetition {} to {}", repetition.getTask().getTitle(), DateUtils.fromDate(day.getDate()));
            });
        }
    }

    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

}
