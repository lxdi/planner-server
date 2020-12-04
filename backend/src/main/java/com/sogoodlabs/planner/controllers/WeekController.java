package com.sogoodlabs.planner.controllers;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.entities.Week;
import com.sogoodlabs.planner.services.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                .map(weekService::fill)
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/get/prev/{currentId}")
    public Map<String, Object> getPrev(@PathVariable("currentId") String currentId){
        Week week = weekService.fill(weekService.getPrev(currentId));
        return commonMapper.mapToDto(week);
    }

    @GetMapping("/get/next/{currentId}")
    public Map<String, Object> getNext(@PathVariable("currentId") String currentId){
        Week week = weekService.fill(weekService.getNext(currentId));
        return commonMapper.mapToDto(week);
    }
}
