package com.sogoodlabs.planner.controllers;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.Week;
import com.sogoodlabs.planner.services.WeekService;
import com.sogoodlabs.planner.services.WeeksGenerator;
import com.sogoodlabs.planner.util.DateUtils;
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

    @Autowired
    private IWeekDAO weekDAO;

    @Autowired
    private WeeksGenerator weeksGenerator;

    @GetMapping("/get/all/current/year")
    public List<Map<String, Object>> current(){
        int year = DateUtils.getYear(DateUtils.currentDate());
        List<Week> weeks = weekDAO.findByYear(year);

        if(weeks == null || weeks.isEmpty()){
            weeksGenerator.generateYear(year);
            weeks = weekDAO.findByYear(year);
        }

        return weeks.stream()
                .peek(weekService::fill)
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
