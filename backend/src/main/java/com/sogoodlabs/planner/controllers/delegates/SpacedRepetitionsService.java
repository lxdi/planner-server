package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.ITaskMappersDAO;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.dto.TasksDtoMapper;
import com.sogoodlabs.planner.model.dto.additional_mapping.AdditionalTasksMapping;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpacedRepetitionsService {

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    public Map<Integer, List<Map<String, Object>>> getActualTaskToRepeat(){
        Map<Integer, List<Map<String, Object>>> result = new HashMap<>();

        Date fromDate = DateUtils.addDays(DateUtils.currentDate(), -3);
        Date toDate = DateUtils.addDays(DateUtils.currentDate(), +3);

        result.putIfAbsent(100, new ArrayList<>());
        getCurrentTasks().forEach(task->result.get(100).add(getTaskDto(task, null)));

        result.putIfAbsent(99, getOutdatedTasksDto());

        List<Repetition> reps = repDAO.getUnFinishedWithPlanDateInRange(DateUtils.addWeeks(fromDate, -2), DateUtils.addWeeks(toDate, 1));

        setTasks(fromDate, toDate, 0, result, reps);
        setTasks(DateUtils.addWeeks(fromDate, 1), DateUtils.addWeeks(toDate, 1), 1, result, reps);
        setTasks(DateUtils.addWeeks(fromDate, -1), DateUtils.addWeeks(toDate, -1), -1, result, reps);
        setTasks(DateUtils.addWeeks(fromDate, -2), DateUtils.addWeeks(toDate, -2), -2, result, reps);

        return result;
    }

    private void setTasks(Date from, Date to, int weeknum, Map<Integer, List<Map<String, Object>>> result, List<Repetition> repetitions){
        result.putIfAbsent(weeknum, new ArrayList<>());
        repetitions.forEach(rep -> {
            if(DateUtils.differenceInDays(rep.getPlanDate(), from)<=0 && DateUtils.differenceInDays(rep.getPlanDate(), to)>=0){
                Task task = rep.getSpacedRepetitions().getTaskMapper().getTask();
                result.get(weeknum).add(getTaskDto(task, rep));
            }
        });

    }

    private Map<String, Object> getTaskDto(Task task, Repetition repetition){
        Map<String, Object> taskDto = tasksDtoMapper.mapToDtoFull(task);
        if(repetition!=null){
            taskDto.put("repetition", commonMapper.mapToDto(repetition, new HashMap<>()));
        }
        return taskDto;
    }

    private List<Task> getCurrentTasks(){
        List<Task> result = new ArrayList<>();
        Week currentWeek = weekDAO.weekOfDate(DateUtils.currentDate());
        if(currentWeek==null){ //TODO make generating weeks while the system starts
            while(currentWeek==null){ //first call, when weeks may be not generated yet by calling hquarters
                currentWeek = weekDAO.weekOfDate(DateUtils.currentDate());
            }
        }
        DaysOfWeek currentDayOfWeek = DaysOfWeek.findById(DateUtils.differenceInDays(currentWeek.getStartDay(), DateUtils.currentDate()));
        List<TaskMapper> taskMappers = taskMappersDAO.byWeekAndDay(currentWeek, currentDayOfWeek);
        taskMappers.forEach(tm->result.add(tm.getTask()));
        return result;
    }

    private List<Map<String, Object>> getOutdatedTasksDto(){
        List<Map<String, Object>> result = new ArrayList<>();
        getOutDatedTasksFromCurrentHq().forEach(task -> result.add(getTaskDto(task, null)));
        return result;
    }

    private List<Task> getOutDatedTasksFromCurrentHq(){
        HQuarter currentHq = ihQuarterDAO.getByDate(DateUtils.currentDate());
        if(currentHq!=null){
            List<TaskMapper> taskMappers = taskMappersDAO.taskMappersOfHqAndBefore(currentHq, DateUtils.currentDate());
            if(taskMappers.size()>0){
                List<Task> result = new ArrayList<>();
                taskMappers.forEach(tm -> {
                    if(tm.getTask()!=null && tm.getFinishDate()==null){
                        result.add(tm.getTask());
                    }
                });
                return result;
            }
        }
        return new ArrayList<>();
    }



}
