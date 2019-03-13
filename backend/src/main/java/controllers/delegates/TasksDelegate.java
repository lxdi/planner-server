package controllers.delegates;

import model.dao.IRepPlanDAO;
import model.dao.ISpacedRepDAO;
import model.dao.ITaskMappersDAO;
import model.dao.ITasksDAO;
import model.dto.task.TaskDtoLazy;
import model.dto.task.TasksDtoMapper;
import model.entities.SpacedRepetitions;
import model.entities.Task;
import model.entities.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class TasksDelegate {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    @Autowired
    IRepPlanDAO repPlanDAO;

    public TaskDtoLazy createTask(TaskDtoLazy taskDto){
        Task task = tasksDtoMapper.mapToEntity(taskDto);
        tasksDAO.saveOrUpdate(task);
        return tasksDtoMapper.mapToDto(task);
    }

    public void delete(long id){
        tasksDAO.delete(id);
    }

    public TaskDtoLazy update(TaskDtoLazy taskDto){
        Task task = tasksDtoMapper.mapToEntity(taskDto);
        tasksDAO.saveOrUpdate(task);
        return tasksDtoMapper.mapToDto(task);
    }

    public void finishTask(int taskid){
        Task task = tasksDAO.getById(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
    }

    public void finishTaskWithRepetition(int taskid, long repid){
        Task task = tasksDAO.getById(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTaskMapper(taskMapper.getId());
        if(spacedRepetitions == null){
            spacedRepetitions = new SpacedRepetitions();
            spacedRepetitions.setTaskMapper(taskMapper);
            spacedRepetitions.setRepetitionPlan(repPlanDAO.getById(repid));
            spacedRepDAO.save(spacedRepetitions);
        } else {
            //TODO clean spacedRep
        }
    }

    private void finishTask(TaskMapper taskMapper){
        if(taskMapper==null){
            throw new NullPointerException("There must be a taskMapper for the task");
        }
        taskMapper.setFinishDate(new Date(new java.util.Date().getTime()));
        taskMappersDAO.saveOrUpdate(taskMapper);
    }

}
