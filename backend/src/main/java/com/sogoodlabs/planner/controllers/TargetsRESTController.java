package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.controllers.delegates.TargetsDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 23.02.2018.
 */

@Controller
public class TargetsRESTController {

    @Autowired
    TargetsDelegate targetsDelegate;

    public TargetsRESTController(){}

    public TargetsRESTController(TargetsDelegate delegate){
        this.targetsDelegate = delegate;
    }

    @RequestMapping(path = "/target/all/lazy")
    public ResponseEntity<List<Map<String, Object>>> getAllTargets(){
        return new ResponseEntity<>(targetsDelegate.getAllTargets(), HttpStatus.OK);
    }

    @RequestMapping(path = "/target/create" , method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> createTarget(@RequestBody Map<String, Object> targetDto) {
        return new ResponseEntity<Map<String, Object>>(targetsDelegate.createTarget(targetDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/target/delete/{targetId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("targetId") long id){
        try {
            targetsDelegate.delete(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/target/update" , method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> update(@RequestBody Map<String, Object> targetDto) {
        return new ResponseEntity<>(targetsDelegate.update(targetDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/target/update/list" , method = RequestMethod.POST)
    public ResponseEntity<List<Map<String, Object>>> updateList(@RequestBody List<Map<String, Object>> targetDtoLazies){
        return new ResponseEntity<>(targetsDelegate.updateList(targetDtoLazies), HttpStatus.OK);
    }

}
