package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.controllers.dto.ActualActivityDto;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.services.ActualActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 27.04.2018.
 */

@RestController
@RequestMapping(path = "/task")
public class TasksRESTController {

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private ActualActivityService actualActivityService;

    @GetMapping("/get/by/mean/{meanid}")
    public List<Map<String, Object>> layersOfMean(@PathVariable("meanid") String meanid){
        Mean mean = meansDAO.findById(meanid).orElseThrow(() -> new RuntimeException("Mean not found by " + meanid));

        return tasksDAO.findByMean(mean).stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/get/actual")
    public Map<String, Object> getActual(){
        return commonMapper.mapToDto(actualActivityService.getActualActivity());
    }

}
