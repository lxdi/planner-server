package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.services.GracefulDeleteService;
import com.sogoodlabs.planner.services.MeanFillerService;
import com.sogoodlabs.planner.services.MeansService;
import com.sogoodlabs.planner.services.RepositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 23.02.2018.
 */

@RestController
@RequestMapping(path = "/mean")
public class MeansRESTController {

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;

    @Autowired
    private MeansService meansService;

    @Autowired
    private MeanFillerService meanFillerService;

    @Autowired
    private RepositionService repositionService;

    @GetMapping("/get/all")
    public List<Map<String, Object>> getAllTargets(){
        return meansDAO.findAll().stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/get/full/{meanid}")
    public Map<String, Object> getAllTargets(@PathVariable("meanid") String meanid){
        Mean mean =  meansDAO.findById(meanid).orElseThrow(() -> new RuntimeException("Mean not found by " + meanid));
        meanFillerService.fill(mean);
        return commonMapper.mapToDto(mean);
    }

    @PutMapping("/create")
    public Map<String, Object> createTarget(@RequestBody Map<String, Object> meanDto) {
        Mean mean = meansService.createMean(commonMapper.mapToEntity(meanDto, new Mean()));
        return commonMapper.mapToDto(mean);
    }

    @DeleteMapping("/delete/{targetId}")
    public void delete(@PathVariable("targetId") String id) {
        Mean mean = meansDAO.findById(id).orElseThrow(() -> new RuntimeException("Mean not found by " + id));
        gracefulDeleteService.deleteMean(mean);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Map<String, Object> meanDto) {
        Mean mean = meansService.updateMean(commonMapper.mapToEntity(meanDto, new Mean()));
        return commonMapper.mapToDto(mean);
    }

    @PostMapping("/update/list")
    public List<Map<String, Object>> updateList(@RequestBody List<Map<String, Object>> meanDtoList){
        return meanDtoList.stream()
                .map(mean -> commonMapper.mapToDto(meansService.updateMean(commonMapper.mapToEntity(mean, new Mean()))))
                .collect(Collectors.toList());
    }

    @PostMapping("/reposition/list")
    public void reposition(@RequestBody List<Map<String, Object>> meanDtoList){
        repositionService.repositionMeans(meanDtoList.stream()
                .map(mean -> commonMapper.mapToEntity(mean, new Mean()))
                .collect(Collectors.toList()));
    }

}
