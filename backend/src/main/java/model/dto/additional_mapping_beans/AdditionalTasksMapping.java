package model.dto.additional_mapping_beans;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ITopicDAO;
import model.entities.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdditionalTasksMapping {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ITopicDAO topicDAO;

    public void fillTopicsInTaskDto(Map<String, Object> taskDto){
        List<Topic> topics = topicDAO.getByTaskId((Long) taskDto.get("id"));
        if(topics.size()>0){
            taskDto.putIfAbsent("topics", new ArrayList<>());
            topics.forEach(topic->((List)taskDto.get("topics")).add(commonMapper.mapToDto(topic, new HashMap<>())));
        }
    }

}
