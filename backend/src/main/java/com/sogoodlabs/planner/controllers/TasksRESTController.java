package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 27.04.2018.
 */

@RestController
@RequestMapping(path = "/tasks")
public class TasksRESTController {

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private CommonMapper commonMapper;

    @GetMapping
    public List<Map<String, Object>> layersOfMean(@RequestParam("mean-id") String meanid){
        Mean mean = meansDAO.findById(meanid).orElseThrow(() -> new RuntimeException("Mean not found by " + meanid));

        return tasksDAO.findByMean(mean).stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
