package com.sogoodlabs.planner.model.dto.additional_mapping;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ITaskMappersDAO;
import com.sogoodlabs.planner.model.dao.ITaskTestingDAO;
import com.sogoodlabs.planner.model.dao.ITopicDAO;
import com.sogoodlabs.planner.model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sogoodlabs.planner.util.StringUtils;

import java.util.*;

@Service
public class AdditionalTasksMapping {

    public static final String LAYER_NAME_PREFIX = "Layer-";
    public static final String FULLNAME_FIELD_NAME = "fullname";
    public static final String TOPICS_FIELD_NAME = "topics";
    public static final String TESTINGS_FIELD_NAME = "testings";
    public static final String FINISHED_FIELD_NAME = "finished";

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ITaskTestingDAO testingDAO;

    public void fillTopicsInTaskDto(Map<String, Object> taskDto, Task task){
        fillList(taskDto, TOPICS_FIELD_NAME, topicDAO.getByTaskId(task.getId()));
    }

    public void fillTestingsInTaskDto(Map<String, Object> taskDto, Task task){
        fillList(taskDto, TESTINGS_FIELD_NAME, testingDAO.getByTaskId(task.getId()));
    }

    public void fillFullName(Map<String, Object> taskDto, Task task){
        String[] expressions = new String[]{
                "subject?.layer?.mean?.realm?.title", "subject?.layer?.mean?.title",
                "'"+LAYER_NAME_PREFIX+"' + subject?.layer?.priority", "subject?.title", "title"
        };
        taskDto.put(FULLNAME_FIELD_NAME, StringUtils.getFullName(task, expressions));
    }

    public void fillIsFinished(Map<String, Object> taskDto, Task task){
        if(this.taskMappersDAO.finishDateByTaskid(task.getId())!=null){
            taskDto.put(FINISHED_FIELD_NAME, true);
        } else {
            taskDto.put(FINISHED_FIELD_NAME, false);
        }
    }

    private void fillList(Map<String, Object> taskDto, String listName, List objectsList){
        taskDto.putIfAbsent(listName, new ArrayList<>());
        if(objectsList.size()>0){
            objectsList.forEach(obj->((List)taskDto.get(listName)).add(commonMapper.mapToDto(obj, new HashMap<>())));
        }
    }

}
