package controllers;

import controllers.delegates.SubjectsDelegate;
import model.dao.ILayerDAO;
import model.dao.ISubjectDAO;
import model.dao.ITasksDAO;
import model.dto.subject.SubjectDtoLazy;
import model.dto.subject.SubjectDtoMapper;
import model.dto.task.TaskDtoLazy;
import model.entities.Subject;
import model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<List<SubjectDtoLazy>> layersOfMean(@PathVariable("layerid") long layerid){
        return new ResponseEntity<List<SubjectDtoLazy>>(subjectsDelegate.layersOfMean(layerid), HttpStatus.OK);
    }

    @RequestMapping(path = "/delete/{subjectid}" , method = RequestMethod.DELETE)
    public ResponseEntity<SubjectDtoLazy> delete(@PathVariable("subjectid") long subjectid){
            return new ResponseEntity<>(subjectsDelegate.delete(subjectid), HttpStatus.OK);
    }

}
