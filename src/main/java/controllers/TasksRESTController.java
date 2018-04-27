package controllers;

import model.ITasksDAO;
import model.dto.task.TaskDtoLazy;
import model.dto.task.TasksDtoMapper;
import model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    public TasksRESTController(){
    }

    public TasksRESTController(ITasksDAO tasksDAO, TasksDtoMapper tasksDtoMapper){
        this.tasksDAO = tasksDAO;
        this.tasksDtoMapper = tasksDtoMapper;
    }

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
        try {
            tasksDAO.delete(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/task/update" , method = RequestMethod.POST)
    public ResponseEntity<TaskDtoLazy> update(@RequestBody TaskDtoLazy taskDto){
        Task task = tasksDtoMapper.mapToEntity(taskDto);
        tasksDAO.saveOrUpdate(task);
        return new ResponseEntity<TaskDtoLazy>(tasksDtoMapper.mapToDto(task), HttpStatus.OK);
    }

}
