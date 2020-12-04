package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.controllers.dto.TaskProgressDto;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.ITaskMappersDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private IRepDAO repDAO;

    public TaskProgressDto getByTask(Task task){
        TaskProgressDto taskProgressDto = new TaskProgressDto();

        taskProgressDto.setTaskMappers(taskMappersDAO.findByTask(task));
        taskProgressDto.setRepetitions(repDAO.findByTask(task));

        return taskProgressDto;
    }

}
