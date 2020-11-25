package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ITargetsDAO;
import com.sogoodlabs.planner.model.entities.Target;
import com.sogoodlabs.planner.services.GracefulDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 23.02.2018.
 */

@RestController
@RequestMapping(path = "/target")
public class TargetsRESTController {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private ITargetsDAO targetsDAO;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;

    @GetMapping("/get/all")
    public List<Map<String, Object>> getAllTargets(){
        return targetsDAO.findAll().stream()
                .map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/create")
    public Map<String, Object> createTarget(@RequestBody Map<String, Object> targetDto) {
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        target.setId(UUID.randomUUID().toString());
        targetsDAO.save(target);
        return commonMapper.mapToDto(target);
    }

    @DeleteMapping("/delete/{targetId}")
    public void delete(@PathVariable("targetId") String id) {
        Target target = targetsDAO.findById(id).orElseThrow(() -> new RuntimeException("Target not found by " + id));
        gracefulDeleteService.deleteTarget(target);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Map<String, Object> targetDto) {
       return updateOneTarget(targetDto);
    }

    @PostMapping("/update/list")
    public List<Map<String, Object>> updateList(@RequestBody List<Map<String, Object>> targetDtoList){
        return targetDtoList.stream()
                .map(this::updateOneTarget)
                .collect(Collectors.toList());
    }

    private Map<String, Object> updateOneTarget(Map<String, Object> targetDto){
        Target target = commonMapper.mapToEntity(targetDto, new Target());
        targetsDAO.save(target);
        return commonMapper.mapToDto(target);
    }

}
