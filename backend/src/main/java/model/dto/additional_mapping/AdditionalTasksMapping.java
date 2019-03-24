package model.dto.additional_mapping;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ITaskTestingDAO;
import model.dao.ITopicDAO;
import model.entities.Task;
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

    private static final String LAYER_NAME_PREFIX = "Layer-";

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ITaskTestingDAO testingDAO;

    public void fillTopicsInTaskDto(Map<String, Object> taskDto){
        fillList(taskDto, "topics", topicDAO.getByTaskId((Long) taskDto.get("id")));
    }

    public void fillTestingsInTaskDto(Map<String, Object> taskDto){
        fillList(taskDto, "testings", testingDAO.getByTask((Long) taskDto.get("id")));
    }

    public void fillFullName(Map<String, Object> taskDto, Task task){
        String[] expressions = new String[]{
                "subject?.layer?.mean?.realm?.title", "subject?.layer?.mean?.title",
                "'"+LAYER_NAME_PREFIX+"' + subject?.layer?.priority", "subject?.title", "title"
        };
        taskDto.put("fullname", StringUtils.getFullName(task, expressions));
    }

    private void fillList(Map<String, Object> taskDto, String listName, List objectsList){
        if(objectsList.size()>0){
            taskDto.putIfAbsent(listName, new ArrayList<>());
            objectsList.forEach(obj->((List)taskDto.get(listName)).add(commonMapper.mapToDto(obj, new HashMap<>())));
        }
    }

}
