package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/repetition/plan")
public class RepPlansController {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private IRepPlanDAO repPlanDAO;

    @GetMapping("/all")
    public List<Map<String, Object>> getAll(){
        return repPlanDAO.findAll().stream().map(commonMapper::mapToDto).collect(Collectors.toList());
    }

}
