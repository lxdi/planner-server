package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ITaskTestingDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.TaskTesting;
import com.sogoodlabs.planner.services.RepositionService;
import com.sogoodlabs.planner.services.TaskTestingsUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/tasktesting")
public class TestingsController {

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private TaskTestingsUpdateService taskTestingsUpdateService;

    @GetMapping("/get/by/mean/{meanid}")
    public List<Map<String, Object>> layersOfMean(@PathVariable("meanid") String meanid){
        Mean mean = meansDAO.findById(meanid).orElseThrow(() -> new RuntimeException("Mean not found by " + meanid));

        return taskTestingDAO.findByMean(mean).stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/flat/all")
    public void flatTestings(){
        taskTestingsUpdateService.flatAllTestings();
    }
}
