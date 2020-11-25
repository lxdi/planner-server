package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.services.GracefulDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    @GetMapping("/get/all")
    public List<Map<String, Object>> getAllTargets(){
        return meansDAO.findAll().stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/create")
    public Map<String, Object> createTarget(@RequestBody Map<String, Object> meanDto) {
        Mean mean = commonMapper.mapToEntity(meanDto, new Mean());
        mean.setId(UUID.randomUUID().toString());
        meansDAO.save(mean);
        return commonMapper.mapToDto(mean);
    }

    @DeleteMapping("/delete/{targetId}")
    public void delete(@PathVariable("targetId") String id) {
        Mean mean = meansDAO.findById(id).orElseThrow(() -> new RuntimeException("Mean not found by " + id));
        gracefulDeleteService.deleteMean(mean);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Map<String, Object> targetDto) {
        return updateOneMean(targetDto);
    }

    @PostMapping("/update/list")
    public List<Map<String, Object>> updateList(@RequestBody List<Map<String, Object>> meanDtoList){
        return meanDtoList.stream()
                .map(this::updateOneMean)
                .collect(Collectors.toList());
    }

    private Map<String, Object> updateOneMean(Map<String, Object> meanDto){
        Mean mean = commonMapper.mapToEntity(meanDto, new Mean());
        meansDAO.save(mean);
        return commonMapper.mapToDto(mean);
    }

}
