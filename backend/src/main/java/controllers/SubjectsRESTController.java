package controllers;

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
    ISubjectDAO subjectDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    SubjectDtoMapper subjectDtoMapper;

    @Autowired
    ITasksDAO tasksDAO;

    @Deprecated
    @RequestMapping(path = "/get/bylayer/{layerid}" , method = RequestMethod.GET)
    public ResponseEntity<List<SubjectDtoLazy>> layersOfMean(@PathVariable("layerid") long layerid){
        List<SubjectDtoLazy> result = new ArrayList<>();
        for(Subject subject : subjectDAO.subjectsByLayer(layerDAO.layerById(layerid))){
            result.add(subjectDtoMapper.mapToDto(subject));
        }
        return new ResponseEntity<List<SubjectDtoLazy>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/delete/{subjectid}" , method = RequestMethod.DELETE)
    public ResponseEntity<SubjectDtoLazy> delete(@PathVariable("subjectid") long subjectid){
        Subject subject = subjectDAO.getById(subjectid);
        if(subject!=null) {
            List<Task> tasks = tasksDAO.tasksBySubject(subject);
            if (tasks != null && tasks.size() > 0) {
                tasks.forEach(t -> tasksDAO.delete(t.getId()));
            }
            subjectDAO.delete(subject.getId());
            return new ResponseEntity<>(subjectDtoMapper.mapToDto(subject), HttpStatus.OK);
        } else {
            throw new RuntimeException("Subject doesn't exist");
        }
    }

}
