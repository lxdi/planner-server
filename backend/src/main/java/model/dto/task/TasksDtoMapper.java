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
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.StringUtils;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Created by Alexander on 26.04.2018.
 */

@Service
@Transactional
public class TasksDtoMapper implements IMapper<TaskDtoLazy, Task> {

    private static final String LAYER_NAME_PREFIX = "Layer-";

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
        dto.setFullname(getFullname(task));
        if(task.getSubject()!=null){
            dto.setSubjectid(task.getSubject().getId());
        }
        if(task.getTopics()!=null && task.getTopics().size()>0){
            for(Topic topic : task.getTopics()){
                dto.getTopics().add(topicMapper.mapToDto(topic));
            }
        }

//        taskTestingDAO.getByTask(task.getId()).forEach(testing ->
//            dto.getTestings().add(commonMapper.mapToDto(testing, new HashMap<>())));
        task.getTestings().forEach(testing ->
                dto.getTestings().add(commonMapper.mapToDto(testing, new HashMap<>())));

        Date finishDate = taskMappersDAO.finishDateByTaskid(task.getId());
        if(finishDate!=null){
            dto.setFinished(true);
        }
        return dto;
    }

    private String getFullname(Task task){
        String[] expressions = new String[]{
                "subject?.layer?.mean?.realm?.title", "subject?.layer?.mean?.title",
                "'"+LAYER_NAME_PREFIX+"' + subject?.layer?.priority", "subject?.title", "title"
        };
        return StringUtils.getFullName(task, expressions);
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
