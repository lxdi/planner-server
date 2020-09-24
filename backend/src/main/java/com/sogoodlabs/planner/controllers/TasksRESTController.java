package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.services.SpacedRepetitionsService;
import com.sogoodlabs.planner.controllers.delegates.TasksDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Map<String, Object> taskDto){
        return new ResponseEntity<>(tasksDelegate.createTask(taskDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/task/delete/{taskId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("taskId") long id){
        tasksDelegate.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/update" , method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> update(@RequestBody Map<String, Object> taskDto){
        return new ResponseEntity<>(tasksDelegate.update(taskDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/task/{taskid}/finish" , method = RequestMethod.POST)
    public ResponseEntity finishTask(@PathVariable("taskid") int taskid){
        tasksDelegate.finishTask(taskid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/{taskid}/finish/with/repetition/{repPlanId}" , method = RequestMethod.POST)
    public ResponseEntity finishTaskWithRepetition(@PathVariable("taskid") long taskid,
                                                   @PathVariable("repPlanId") long repPlanId,
                                                   @RequestBody List<Map<String, Object>> testingsDto){
        tasksDelegate.finishTaskWithRepetition(taskid, repPlanId, testingsDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/finish/repetition/{repId}" , method = RequestMethod.POST)
    public ResponseEntity finishRepetition(@PathVariable("repId") long repId){
        tasksDelegate.finishRepetition(repId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/finish/repetition/{repId}/and/low/other" , method = RequestMethod.POST)
    public ResponseEntity finishRepetitionWithLowing(@PathVariable("repId") long repId){
        tasksDelegate.finishRepetitionWithLowing(repId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/{taskid}/repetition/all/left/remove" , method = RequestMethod.DELETE)
    public ResponseEntity removeAllRepetitionsLeft(@PathVariable("taskid") long taskid){
        tasksDelegate.removeRepetitionsLeftForTask(taskid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/task/get/to/repeat/all" , method = RequestMethod.GET)
    public ResponseEntity<Map<Integer, List<Map<String, Object>>>> getTasksToRepeat(){
        return new ResponseEntity<>(spacedRepetitionsService.getActualTaskToRepeat(), HttpStatus.OK);
    }

    @RequestMapping(path="/task/{taskid}/details/repetitions", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> getRepetitionsForTask(@PathVariable("taskid") long taskid){
        return tasksDelegate.getRepetitionsForTask(taskid);
    }

//    @RequestMapping(path = "/task/testing/add/{taskid}" , method = RequestMethod.POST)
//    public ResponseEntity<Map<String, Object>> addNewTestingToTask(@PathVariable("taskid") long taskid, @RequestBody Map<String, Object> testingDto){
//        return new ResponseEntity(tasksDelegate.addNewTestingToTask(taskid, testingDto), HttpStatus.OK);
//    }


}
