package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.services.SafeDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.DateUtils;

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
    IRepPlanDAO repPlanDAO;

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    private SafeDeleteService safeDeleteService;

    public Map<String, Object> createTask(Map<String, Object> taskDto){
        Task task = commonMapper.mapToEntity(taskDto, new Task());
        tasksDAO.saveOrUpdate(task);
        return commonMapper.mapToDto(task);
    }

    public void delete(long id){
        safeDeleteService.deleteTask(id);
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
        finishTask(taskMapper);
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTaskMapper(taskMapper.getId());
        if(spacedRepetitions == null){
            spacedRepetitions = new SpacedRepetitions();
            spacedRepetitions.setTaskMapper(taskMapper);
            spacedRepetitions.setRepetitionPlan(repPlanDAO.getOne(repPlanid));
            spacedRepDAO.save(spacedRepetitions);
            planRepetitions(spacedRepetitions);
        } else {
            //TODO clean spacedRep
        }
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

    public Repetition finishRepetition(long repId){
        Repetition repetition = repDAO.getOne(repId);
        repetition.setFactDate(DateUtils.currentDate());
        repDAO.save(repetition);
        return repetition;
    }

    /**
     * Finishes repetition and make repetitionOnly all repetitions after
     * @param repId
     */
    public void finishRepetitionWithLowing(long repId){
        Repetition repetition = finishRepetition(repId);
        repDAO.makeRepOnlyAllUnfinished(repetition.getSpacedRepetitions());
    }


    /**
     *  Removes all the unfinished repetitions related to the task
     *
     */
    public void removeRepetitionsLeftForTask(long taskid){
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(taskid);
        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepetitions.getId());
        repetitions.forEach(rep -> {
            if(rep.getFactDate()==null){
                repDAO.delete(rep);
            }
        });
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
        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(sr.getId());
        return repetitions.stream().map(commonMapper::mapToDto).collect(Collectors.toList());
    }

    private void finishTask(TaskMapper taskMapper){
        if(taskMapper==null){
            throw new NullPointerException("There must be a taskMapper for the task");
        }
        taskMapper.setFinishDate(DateUtils.currentDate());
        taskMappersDAO.saveOrUpdate(taskMapper);
    }

    private List<Repetition> planRepetitions(SpacedRepetitions spacedRepetitions){
        List<Repetition> repetitions = new ArrayList<>();
        RepetitionPlan repetitionPlan = spacedRepetitions.getRepetitionPlan();
        for(int weeksToRep : repetitionPlan.getPlan()){
            Repetition repetition = new Repetition();
            repetition.setSpacedRepetitions(spacedRepetitions);
            Date planDate = DateUtils.addWeeks(spacedRepetitions.getTaskMapper().getFinishDate(), weeksToRep);
            repetition.setPlanDate(planDate);
            repDAO.save(repetition);
        }
        return repetitions;
    }


}
