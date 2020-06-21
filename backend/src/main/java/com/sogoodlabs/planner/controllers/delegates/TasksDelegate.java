package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.DateUtils;

import java.sql.Date;
import java.util.*;

@Service
@Transactional
public class TasksDelegate {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    IRepPlanDAO repPlanDAO;

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    public Map<String, Object> createTask(Map<String, Object> taskDto){
        Task task = commonMapper.mapToEntity(taskDto, new Task());
        tasksDAO.saveOrUpdate(task);
        return commonMapper.mapToDto(task);
    }

    public void delete(long id){
        tasksDAO.delete(id);
    }

    public Map<String, Object> update(Map<String, Object> taskDto){
        Task task = commonMapper.mapToEntity(taskDto, new Task());
        tasksDAO.saveOrUpdate(task);
        return commonMapper.mapToDto(task);
    }

    public void finishTask(long taskid){
        Task task = tasksDAO.getById(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
    }

    public void finishTaskWithRepetition(long taskid, long repPlanid, List<Map<String, Object>> testingsDto){
        Task task = tasksDAO.getById(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        planRepetitions(repPlanDAO.getById(repPlanid));
        finishTask(taskMapper);
        if(testingsDto!=null){
            testingsDto.forEach(testingDto -> {
                if(testingDto.get("id")==null)
                    addNewTestingToTask(taskid, testingDto);
                else {
                    if(testingDto.get("taskid")==null || Long.parseLong(""+testingDto.get("taskid"))!=taskid){
                        throw new UnsupportedOperationException("This taskTesting is not for the current task");
                    }
                    taskTestingDAO.save(commonMapper.mapToEntity(testingDto, new TaskTesting()));
                }
            });
        }
    }

    public void finishRepetition(long repId){
        Repetition repetition = repDAO.findOne(repId);
        //TODO find day by date
        //repetition.setFactDay(DateUtils.currentDate());
        repDAO.save(repetition);
    }

    public Map<String, Object> addNewTestingToTask(long taskid, Map<String, Object> testingDto){
        if(testingDto!=null) {
            if (testingDto.get("id") != null) {
                throw new UnsupportedOperationException("Testing to add must be new");
            }
            testingDto.put("taskid", taskid);
            TaskTesting taskTesting = commonMapper.mapToEntity(testingDto, new TaskTesting());
            if (taskTesting.getTask() == null) {
                throw new NullPointerException("TaskTesting must have a task");
            }
            taskTestingDAO.save(taskTesting);
            return commonMapper.mapToDto(taskTesting, new HashMap<>());
        } else return null;
    }

    private void finishTask(TaskMapper taskMapper){
        if(taskMapper==null){
            throw new NullPointerException("There must be a taskMapper for the task");
        }
        //TODO find day by date
        //taskMapper.setFactDay(DateUtils.currentDate());
        taskMappersDAO.saveOrUpdate(taskMapper);
    }

    private List<Repetition> planRepetitions(RepetitionPlan repetitionPlan){
        List<Repetition> repetitions = new ArrayList<>();
        for(int weeksToRep : repetitionPlan.getPlan()){
            Repetition repetition = new Repetition();
            Date planDate = DateUtils.addWeeks(DateUtils.currentDate(), weeksToRep);
            //TODO find day by date
            //repetition.setPlanDay();
            repDAO.save(repetition);
        }
        return repetitions;
    }


}
