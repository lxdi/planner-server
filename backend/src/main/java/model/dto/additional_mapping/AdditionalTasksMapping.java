package model.dto.additional_mapping;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ITaskMappersDAO;
import model.dao.ITaskTestingDAO;
import model.dao.ITopicDAO;
import model.entities.Task;
import model.entities.TaskMapper;
import model.entities.TaskTesting;
import model.entities.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ITopicDAO topicDAO;

    @Autowired
    ITaskTestingDAO testingDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    public void fillTopicsInTaskDto(Map<String, Object> taskDto){
        fillList(taskDto, TOPICS_FIELD_NAME, topicDAO.getByTaskId((Long) taskDto.get("id")));
    }

    public void fillTestingsInTaskDto(Map<String, Object> taskDto){
        fillList(taskDto, TESTINGS_FIELD_NAME, testingDAO.getByTask((Long) taskDto.get("id")));
    }

    public void fillFullName(Map<String, Object> taskDto, Task task){
        String[] expressions = new String[]{
                "subject?.layer?.mean?.realm?.title", "subject?.layer?.mean?.title",
                "'"+LAYER_NAME_PREFIX+"' + subject?.layer?.priority", "subject?.title", "title"
        };
        taskDto.put(FULLNAME_FIELD_NAME, StringUtils.getFullName(task, expressions));
    }

    public void fillIsFinished(Task task, Map<String, Object> taskDto){
        if(this.taskMappersDAO.finishDateByTaskid(task.getId())!=null){
            taskDto.put(FINISHED_FIELD_NAME, true);
        } else {
            taskDto.put(FINISHED_FIELD_NAME, false);
        }
    }

    private void fillList(Map<String, Object> taskDto, String listName, List objectsList){
        if(objectsList.size()>0){
            taskDto.putIfAbsent(listName, new ArrayList<>());
            objectsList.forEach(obj->((List)taskDto.get(listName)).add(commonMapper.mapToDto(obj, new HashMap<>())));
        }
    }

}
