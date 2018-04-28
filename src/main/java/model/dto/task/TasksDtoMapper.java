package model.dto.task;

import model.IMeansDAO;
import model.ITasksDAO;
import model.IWeekDAO;
import model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Alexander on 26.04.2018.
 */

@Service
public class TasksDtoMapper {

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    IMeansDAO meansDAO;

    public TaskDtoLazy mapToDto(Task task){
        TaskDtoLazy dto = new TaskDtoLazy();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        if(task.getMean()!=null)
            dto.setMeanid(task.getMean().getId());
        if(task.getWeek()!=null)
            dto.setWeekid(task.getWeek().getId());
        return dto;
    }

    public Task mapToEntity(TaskDtoLazy dto){
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        if(dto.getMeanid()!=null){
            task.setMean(meansDAO.meanById(dto.getMeanid()));
        }
        if(dto.getWeekid()!=null){
            task.setWeek(weekDAO.getById(dto.getWeekid()));
        }
        return task;
    }

}
