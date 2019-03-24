package model.dto.task;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ISubjectDAO;
import model.dao.ITaskMappersDAO;
import model.dao.ITaskTestingDAO;
import model.dto.IMapper;
import model.dto.topic.TopicDto;
import model.dto.topic.TopicMapper;
import model.entities.Task;
import model.entities.TaskTesting;
import model.entities.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alexander on 26.04.2018.
 */

@Service
public class TasksDtoMapper implements IMapper<TaskDtoLazy, Task> {

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    CommonMapper commonMapper;

    public TaskDtoLazy mapToDto(Task task){
        TaskDtoLazy dto = new TaskDtoLazy();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setPosition(task.getPosition());
        if(task.getSubject()!=null){
            dto.setSubjectid(task.getSubject().getId());
        }
        if(task.getTopics()!=null && task.getTopics().size()>0){
            for(Topic topic : task.getTopics()){
                dto.getTopics().add(topicMapper.mapToDto(topic));
            }
        }

        taskTestingDAO.getByTask(task.getId()).forEach(testing ->
            dto.getTestings().add(commonMapper.mapToDto(testing, new HashMap<>())));

        Date finishDate = taskMappersDAO.finishDateByTaskid(task.getId());
        if(finishDate!=null){
            dto.setFinished(true);
        }
        return dto;
    }

    public Task mapToEntity(TaskDtoLazy dto){
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setPosition(dto.getPosition());
        if(dto.getSubjectid()!=null){
            task.setSubject(subjectDAO.getById(dto.getSubjectid()));
        }
        if(dto.getTopics()!=null && dto.getTopics().size()>0){
            for(TopicDto topicDto : dto.getTopics()){
                task.getTopics().add(topicMapper.mapToEntity(topicDto, task));
            }
        }
        return task;
    }

}
