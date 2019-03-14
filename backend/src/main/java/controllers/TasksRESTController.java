package controllers;

import controllers.delegates.SpacedRepetitionsService;
import controllers.delegates.TasksDelegate;
import model.dto.task.TaskDtoLazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 27.04.2018.
 */

@Controller
public class TasksRESTController {

    @Autowired
    TasksDelegate tasksDelegate;

    @Autowired
    SpacedRepetitionsService spacedRepetitionsService;

    public TasksRESTController(){
    }

    public TasksRESTController(TasksDelegate tasksDelegate){
        this.tasksDelegate = tasksDelegate;
    }

    @RequestMapping(path = "/task/create" , method = RequestMethod.PUT)
    public ResponseEntity<TaskDtoLazy> createTask(@RequestBody TaskDtoLazy taskDto){
        return new ResponseEntity<TaskDtoLazy>(tasksDelegate.createTask(taskDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/task/delete/{taskId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("taskId") long id){
        tasksDelegate.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/update" , method = RequestMethod.POST)
    public ResponseEntity<TaskDtoLazy> update(@RequestBody TaskDtoLazy taskDto){
        return new ResponseEntity<TaskDtoLazy>(tasksDelegate.update(taskDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/task/{taskid}/finish" , method = RequestMethod.POST)
    public ResponseEntity finishTask(@PathVariable("taskid") int taskid){
        tasksDelegate.finishTask(taskid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/{taskid}/finish/with/repetition/{repid}" , method = RequestMethod.POST)
    public ResponseEntity finishTaskWithRepetition(@PathVariable("taskid") int taskid, @PathVariable("repid") long repid){
        tasksDelegate.finishTaskWithRepetition(taskid, repid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/get/to/repeat/all" , method = RequestMethod.GET)
    public ResponseEntity<Map<Integer, List<TaskDtoLazy>>> getTasksToRepeat(){
        return new ResponseEntity<>(spacedRepetitionsService.getActualTaskToRepeat(), HttpStatus.OK);
    }


}
