package com.sogoodlabs.planner.controllers;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.services.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/week")
public class WeekController {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private WeekService weekService;


    @GetMapping("/get/all/current/year")
    public List<Map<String, Object>> current(){
        return weekService.getCurrent().stream()
                .peek(weekService::fill)
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
