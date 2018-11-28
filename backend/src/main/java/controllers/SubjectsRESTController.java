package controllers;

import model.dao.ILayerDAO;
import model.dao.ISubjectDAO;
import model.dto.subject.SubjectDtoLazy;
import model.dto.subject.SubjectDtoMapper;
import model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@RequestMapping(path="/subject")
public class SubjectsRESTController {

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    SubjectDtoMapper subjectDtoMapper;

    @RequestMapping(path = "/get/bylayer/{layerid}" , method = RequestMethod.GET)
    public ResponseEntity<List<SubjectDtoLazy>> layersOfMean(@PathVariable("layerid") long layerid){
        List<SubjectDtoLazy> result = new ArrayList<>();
        for(Subject subject : subjectDAO.subjectsByLayer(layerDAO.layerById(layerid))){
            result.add(subjectDtoMapper.mapToDto(subject));
        }
        return new ResponseEntity<List<SubjectDtoLazy>>(result, HttpStatus.OK);
    }

}
