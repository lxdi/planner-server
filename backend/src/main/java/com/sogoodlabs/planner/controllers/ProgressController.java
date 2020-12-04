package com.sogoodlabs.planner.controllers;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.configuration.main.CommonMapperConfigs;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/get/for/task/{taskid}")
    public Map<String, Object> getForTask(@PathVariable("taskid") String taskId){
        Task task = tasksDAO.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found " + taskId));
        return commonMapper.mapToDto(progressService.getByTask(task));
    }

}
