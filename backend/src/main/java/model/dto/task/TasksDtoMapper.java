package model.dto.task;

import model.dao.ISubjectDAO;
import model.dto.IMapper;
import model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Alexander on 26.04.2018.
 */

@Service
public class TasksDtoMapper implements IMapper<TaskDtoLazy, Task> {

    @Autowired
    ISubjectDAO subjectDAO;

    public TaskDtoLazy mapToDto(Task task){
        TaskDtoLazy dto = new TaskDtoLazy();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setPosition(task.getPosition());
        if(task.getSubject()!=null){
            dto.setSubjectid(task.getSubject().getId());
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
        return task;
    }

}
