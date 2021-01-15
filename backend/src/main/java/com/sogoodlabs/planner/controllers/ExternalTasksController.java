package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IExternalTaskDao;
import com.sogoodlabs.planner.model.entities.ExternalTask;
import com.sogoodlabs.planner.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/external/task")
public class ExternalTasksController {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private IExternalTaskDao externalTaskDao;

    @PostMapping("/update")
    public void updateExternalTask(@RequestBody Map<String, Object> dto){
        ExternalTask externalTask = commonMapper.mapToEntity(dto, new ExternalTask());

        if(!IdUtils.isUUID(externalTask.getId())){
            throw new RuntimeException("ExternalTask with id: " + externalTask.getId() + " cannot be updated");
        }

        externalTaskDao.save(externalTask);
    }

    @PutMapping("/create")
    public void createExternalTask(@RequestBody Map<String, Object> dto){
        ExternalTask externalTask = commonMapper.mapToEntity(dto, new ExternalTask());

        if(IdUtils.isUUID(externalTask.getId()) && externalTaskDao.findById(externalTask.getId()).isPresent()){
            throw new RuntimeException("ExternalTask with id: " + externalTask.getId() + " is already exist");
        }

        externalTask.setId(UUID.randomUUID().toString());
        externalTaskDao.save(externalTask);
    }

}
