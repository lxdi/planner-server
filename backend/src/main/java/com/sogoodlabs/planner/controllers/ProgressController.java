package com.sogoodlabs.planner.controllers;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.services.ActualActivityService;
import com.sogoodlabs.planner.services.ProgressService;
import com.sogoodlabs.planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/progress")
public class ProgressController {

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private IRepPlanDAO repPlanDAO;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ActualActivityService actualActivityService;

    @GetMapping
    public Map<String, Object> getForTask(@RequestParam("task-id") String taskId){
        Task task = tasksDAO.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found " + taskId));
        return commonMapper.mapToDto(progressService.getByTask(task));
    }

    @PostMapping(value = "/finished", params = {"task-id"})
    public Map<String, Object> finishTask(@RequestParam("task-id") String taskId){
        Task task = tasksDAO.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found "+ taskId));
        progressService.finishTask(task, null, DateUtils.currentDate());
        return commonMapper.mapToDto(progressService.getByTask(task));
    }

    @PostMapping(value = "/finished", params = {"task-id", "plan-id"})
    public Map<String, Object> finishTaskWithSP(@RequestParam("task-id") String taskId, @RequestParam("plan-id") String planId){
        Task task = tasksDAO.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found "+ taskId));
        RepetitionPlan plan = repPlanDAO.findById(planId).orElseThrow(() -> new RuntimeException("RepPlan not found "+ repPlanDAO));
        progressService.finishTask(task, plan, DateUtils.currentDate());
        return commonMapper.mapToDto(progressService.getByTask(task));
    }

    @PostMapping(value = "/finished", params = "rep-id")
    public Map<String, Object> finishRepetition(@RequestParam("rep-id") String repId){
        Repetition repetition = repDAO.findById(repId).orElseThrow(() -> new RuntimeException("Repetition not found "+ repId));
        progressService.finishRepetition(repetition);
        return commonMapper.mapToDto(progressService.getByTask(repetition.getTask()));
    }

    @DeleteMapping("/unfinished/repetitions")
    public void deleteUnfinished(@RequestParam("task-id") String taskId){
        progressService.removeUnfinishedReps(taskId);
    }

    @GetMapping("/actual")
    public Map<String, Object> getActual(){
        return commonMapper.mapToDto(actualActivityService.getActualActivity());
    }

}
