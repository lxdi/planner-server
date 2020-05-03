package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.controllers.delegates.SubjectsDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path="/subject")
public class SubjectsRESTController {

    @Autowired
    SubjectsDelegate subjectsDelegate;

    public SubjectsRESTController(){}

    public SubjectsRESTController(SubjectsDelegate subjectsDelegate){
        this.subjectsDelegate = subjectsDelegate;
    }


    @Deprecated
    @RequestMapping(path = "/get/bylayer/{layerid}" , method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> layersOfMean(@PathVariable("layerid") long layerid){
        return new ResponseEntity<>(subjectsDelegate.layersOfMean(layerid), HttpStatus.OK);
    }

    @RequestMapping(path = "/delete/{subjectid}" , method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("subjectid") long subjectid){
            return new ResponseEntity<>(subjectsDelegate.delete(subjectid), HttpStatus.OK);
    }

}
