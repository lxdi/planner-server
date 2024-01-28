package com.sogoodlabs.planner.controllers;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.dto.AssignMeanDto;
import com.sogoodlabs.planner.model.dto.MovingPlansDto;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Week;
import com.sogoodlabs.planner.services.ScheduleMeanService;
import com.sogoodlabs.planner.services.UnscheduleService;
import com.sogoodlabs.planner.services.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/weeks")
public class WeekController {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private WeekService weekService;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private ScheduleMeanService scheduleMeanService;

    @Autowired
    private UnscheduleService unscheduleService;

    @Autowired
    private IWeekDAO weekDAO;


    @GetMapping("/current-year")
    public List<Map<String, Object>> current(){
        return weekService.getCurrent().stream()
                .map(weekService::fill)
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{currentId}/prev")
    public Map<String, Object> getPrev(@PathVariable("currentId") String currentId){
        Week week = weekService.fill(weekService.getPrev(currentId));
        return commonMapper.mapToDto(week);
    }

    @GetMapping("/{currentId}/next")
    public Map<String, Object> getNext(@PathVariable("currentId") String currentId){
        Week week = weekService.fill(weekService.getNext(currentId));
        return commonMapper.mapToDto(week);
    }

    @GetMapping("/days/{dayId}")
    public Map<String, Object> getDay(@PathVariable("dayId") String dayId){
        Day day = dayDao.findById(dayId).orElseThrow(() -> new RuntimeException("Day not found by id " + dayId));
        return commonMapper.mapToDto(weekService.getScheduledDayDto(day));
    }

    @GetMapping("/days/by/week/{weekId}")
    public List<Map<String, Object>> getDays(@PathVariable("weekId") String weekId){

        var week = weekDAO.findById(weekId).orElseThrow(() -> new RuntimeException("Week not found by id " + weekId));

        return dayDao.findByWeek(week).stream()
                .map(day -> commonMapper.mapToDto(weekService.getScheduledDayDto(day)))
                .toList();
    }



    @PostMapping("/move/plans")
    public void movePlans(@RequestBody MovingPlansDto movingPlansDto){
        weekService.movePlans(movingPlansDto);
    }

    @PostMapping("/schedule/mean")
    public void scheduleMean(@RequestBody AssignMeanDto assignMeanDto){
        scheduleMeanService.schedule(assignMeanDto);
    }

    @PostMapping("/unschedule/mean/{id}")
    public void unscheduleMean(@PathVariable("id") String meanid){
        unscheduleService.unscheduleMean(meanid);
    }

    @PostMapping("/unschedule/task/{id}")
    public void unscheduleTask(@PathVariable("id") String taskId){
        unscheduleService.unscheduleMean(taskId);
    }

    @PostMapping("/unschedule/layer")
    public void unscheduleLayer(@PathVariable("id") String layerid){
        unscheduleService.unscheduleLayer(layerid);
    }
}
