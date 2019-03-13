package controllers;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 27.04.2018.
 */

@Controller
public class TasksRESTController {

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

    public TasksRESTController(){
    }

    public TasksRESTController(ITasksDAO tasksDAO, TasksDtoMapper tasksDtoMapper){
        this.tasksDAO = tasksDAO;
        this.tasksDtoMapper = tasksDtoMapper;
    }

    @Deprecated
    @RequestMapping(path = "/task/all")
    public ResponseEntity<List<TaskDtoLazy>> getAllTasks(){
        List<TaskDtoLazy> result = new ArrayList<>();
        tasksDAO.allTasks().forEach(t -> result.add(tasksDtoMapper.mapToDto(t)));
        return new ResponseEntity<List<TaskDtoLazy>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/task/create" , method = RequestMethod.PUT)
    public ResponseEntity<TaskDtoLazy> createTask(@RequestBody TaskDtoLazy taskDto){
        Task task = tasksDtoMapper.mapToEntity(taskDto);
        tasksDAO.saveOrUpdate(task);
        return new ResponseEntity<TaskDtoLazy>(tasksDtoMapper.mapToDto(task), HttpStatus.OK);
    }

    @RequestMapping(path = "/task/delete/{taskId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("taskId") long id){
        tasksDAO.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/update" , method = RequestMethod.POST)
    public ResponseEntity<TaskDtoLazy> update(@RequestBody TaskDtoLazy taskDto){
        Task task = tasksDtoMapper.mapToEntity(taskDto);
        tasksDAO.saveOrUpdate(task);
        return new ResponseEntity<TaskDtoLazy>(tasksDtoMapper.mapToDto(task), HttpStatus.OK);
    }

    @RequestMapping(path = "/task/{taskid}/finish" , method = RequestMethod.POST)
    public ResponseEntity finishTask(@PathVariable("taskid") int taskid){
        Task task = tasksDAO.getById(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/{taskid}/finish/with/repetition/{repid}" , method = RequestMethod.POST)
    public ResponseEntity finishTaskWithRepetition(@PathVariable("taskid") int taskid, @PathVariable("repid") long repid){
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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void finishTask(TaskMapper taskMapper){
        if(taskMapper==null){
            throw new NullPointerException("There must be a taskMapper for the task");
        }
        taskMapper.setFinishDate(new Date(new java.util.Date().getTime()));
        taskMappersDAO.saveOrUpdate(taskMapper);
    }

}
