package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.services.RepetitionsPlannerService;
import com.sogoodlabs.planner.services.SafeDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.util.DateUtils;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TasksDelegate {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    private SafeDeleteService safeDeleteService;

    @Autowired
    private RepetitionsPlannerService repetitionsPlannerService;

    public Map<String, Object> createTask(Map<String, Object> taskDto){
        Task task = commonMapper.mapToEntity(taskDto, new Task());
        tasksDAO.save(task);
        return commonMapper.mapToDto(task);
    }

    public void delete(long id){
        safeDeleteService.deleteTask(id);
    }

    public Map<String, Object> update(Map<String, Object> taskDto){
        Task task = commonMapper.mapToEntity(taskDto, new Task());
        tasksDAO.save(task);
        return commonMapper.mapToDto(task);
    }

    public void finishTask(long taskid){
        Task task = tasksDAO.getOne(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
    }

    public void finishTaskWithRepetition(long taskid, long repPlanid, List<Map<String, Object>> testingsDto){
        Task task = tasksDAO.getOne(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
        repetitionsPlannerService.getOrCreateSpacedRepetition(taskMapper, repPlanid);
        saveTestings(taskid, testingsDto);
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

    public List<Map<String, Object>> getRepetitionsForTask(long taskid){
        SpacedRepetitions sr = spacedRepDAO.getSRforTask(taskid);
        if(sr == null){
            return new ArrayList<>();
        }
        return repDAO.getRepsbySpacedRepId(sr.getId()).stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private void finishTask(TaskMapper taskMapper){
        if(taskMapper==null){
            throw new NullPointerException("There must be a taskMapper for the task");
        }
        taskMapper.setFinishDate(DateUtils.currentDate());
        taskMappersDAO.saveOrUpdate(taskMapper);
    }

    private void saveTestings(long taskid, List<Map<String, Object>> testingsDto){
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

}
