package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.dto.AssignLayerDto;
import com.sogoodlabs.planner.dto.AssignMeanDto;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.SortUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScheduleMeanService {

    Logger log = LoggerFactory.getLogger(ScheduleMeanService.class);

    private static final String PLACEHOLDER_TITLE = "Placeholder";

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ILayerDAO layerDAO;

    public void schedule(AssignMeanDto assignMeanDto){
        if (assignMeanDto.getLayers() == null || assignMeanDto.getLayers().isEmpty()) {
            log.warn("No tasks to assign");
            return;
        }

        List<Task> tasks = assignMeanDto.getLayers().stream()
                .flatMap(this::getTasks)
                .collect(Collectors.toList());

        if(tasks.isEmpty()){
            log.warn("No tasks to assign");
            return;
        }

        Day day = dayDao.findById(assignMeanDto.getStartDayId())
                .orElseThrow(()->new RuntimeException("Day not found by id " + assignMeanDto.getStartDayId()));

        schedule(tasks, day, assignMeanDto.getTasksPerWeek());
    }

    private Stream<Task> getTasks(AssignLayerDto assignLayerDto){
        List<Task> result = assignLayerDto.getTaskIds().stream()
                .map(taskId -> tasksDAO.findById(taskId).orElseThrow(()-> new RuntimeException("No Task by id " + taskId)))
                .collect(Collectors.toList());

        if(assignLayerDto.getPlaceholders()<1){
            return result.stream();
        }

        Layer layer = layerDAO.findById(assignLayerDto.getLayerId())
                .orElseThrow(()->new RuntimeException("Layer not found by id " + assignLayerDto.getLayerId()));

        for (int i = 0; i < assignLayerDto.getPlaceholders(); i++) {
            result.add(createPlaceholderTask(layer, i));
        }

        return result.stream();
    }

    private Task createPlaceholderTask(Layer layer, int num){
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setLayer(layer);
        task.setTitle(PLACEHOLDER_TITLE + " " + (num+1));
        tasksDAO.save(task);
        return task;
    }

    private void schedule(List<Task> tasks, Day startOn, int daysPerWeek){
        LinkedList<Task> taskStack = new LinkedList<>(tasks);
        Week currentWeek = startOn.getWeek();
        schedule(taskStack, currentWeek, daysPerWeek, startOn.getWeekDay());
        currentWeek = currentWeek.getNext();

        while(!taskStack.isEmpty()){
            schedule(taskStack, currentWeek, daysPerWeek);
            currentWeek = currentWeek.getNext();
            //TODO handle nextWeek == null
        }
    }

    private void schedule(LinkedList<Task> tasks, Week week, int slots){
        schedule(tasks, week, slots, null);
    }

    private void schedule(LinkedList<Task> tasks, Week week, int slots, DaysOfWeek notBeforeDay){
        List<Day> days = availableDaysToSchedule(week, notBeforeDay);

        for(Day day : days){
            if(slots<1){
                break;
            }

            Task task = tasks.pop();
            TaskMapper taskMapper = new TaskMapper();
            taskMapper.setId(UUID.randomUUID().toString());
            taskMapper.setTask(task);
            taskMapper.setPlanDay(day);
            taskMappersDAO.save(taskMapper);
            slots--;

            if(tasks.isEmpty()){
                break;
            }
        }
    }


    private List<Day> availableDaysToSchedule(Week week, DaysOfWeek notBeforeDay){
        DaysOfWeek notBeforeDayFinal = notBeforeDay == null?DaysOfWeek.mon: notBeforeDay;

        List<Day> allDays =  dayDao.findByWeek(week).stream()
                .filter(day -> day.getWeekDay().getId()>=notBeforeDayFinal.getId())
                .filter(this::isDayAvailableToSchedule)
                .collect(Collectors.toList());

        return SortUtils.sortDistantDays(allDays);
    }

    private boolean isDayAvailableToSchedule(Day day){
        int tasksNum = taskMappersDAO.findByPlanDay(day).size();
        boolean isWeekend = day.getWeekDay()==DaysOfWeek.sat || day.getWeekDay()==DaysOfWeek.sun;

        if(isWeekend && tasksNum>1){
            return false;
        }

        if(!isWeekend && tasksNum>0){
            return false;
        }

        int repetitionsNum = repDAO.findByPlanDay(day).size();

        if(isWeekend && repetitionsNum>2){
            return false;
        }

        if(!isWeekend && repetitionsNum>0){
            return false;
        }

        return true;
    }

}
