package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.dto.additional_mapping.AdditionalTasksMapping;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.TaskTesting;
import com.sogoodlabs.planner.model.entities.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TasksDtoMapper {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    AdditionalTasksMapping additionalTasksMapping;

    @Autowired
    ITasksDAO tasksDAO;

    public Map<String, Object> mapToDtoFull(Task task){
        Map<String, Object> taskDto = commonMapper.mapToDto(task);
        additionalTasksMapping.fillTopicsInTaskDto(taskDto, task);
        additionalTasksMapping.fillTestingsInTaskDto(taskDto, task);
        additionalTasksMapping.fillIsFinished(taskDto, task);
        additionalTasksMapping.fillFullName(taskDto, task);
        return taskDto;
    }

    public Task mapToEntity(Map<String, Object> dto){
        Task task = commonMapper.mapToEntity(dto, new Task());
//        if(dto.get("topics")!=null){
//            ((List)dto.get("topics")).forEach(topicDto -> {
//                task.getTopics().add(commonMapper.mapToEntity((Map<String, Object>) topicDto, new Topic()));
//            });
//        }
//        if(dto.get("testings")!=null){
//            ((List)dto.get("testings")).forEach(testingDto -> {
//                task.getTestings().add(commonMapper.mapToEntity((Map<String, Object>) testingDto, new TaskTesting()));
//            });
//        }
        return task;
    }

}