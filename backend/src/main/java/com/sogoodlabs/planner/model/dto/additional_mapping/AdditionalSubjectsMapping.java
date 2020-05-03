package com.sogoodlabs.planner.model.dto.additional_mapping;

import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.dto.TasksDtoMapper;
import com.sogoodlabs.planner.model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdditionalSubjectsMapping {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    public void mapTasks(Subject subject, Map<String, Object> result){
        result.putIfAbsent("tasks", new ArrayList<>());
        tasksDAO.tasksBySubject(subject).forEach(task ->
            ((List)result.get("tasks")).add(tasksDtoMapper.mapToDtoFull(task)));
    }

}
