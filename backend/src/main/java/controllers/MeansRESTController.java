package controllers;

import controllers.delegates.MeansDelegate;
import controllers.delegates.TaskMappersController;
import model.dao.*;
import model.dto.layer.LayerDtoLazy;
import model.dto.layer.LayersDtoMapper;
import model.dto.mean.MeanDtoFull;
import model.dto.mean.MeanDtoLazy;
import model.dto.mean.MeansDtoFullMapper;
import model.dto.mean.MeansDtoLazyMapper;
import model.dto.subject.SubjectDtoLazy;
import model.dto.subject.SubjectDtoMapper;
import model.dto.task.TaskDtoLazy;
import model.dto.task.TasksDtoMapper;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Realm;
import model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
 * Created by Alexander on 23.02.2018.
 */

@Controller
public class MeansRESTController {

    @Autowired
    MeansDelegate meansDelegate;

    public MeansRESTController(){}

    public MeansRESTController(MeansDelegate meansDelegate){
        this.meansDelegate = meansDelegate;
    }

    @RequestMapping(path = "/mean/all/lazy")
    public ResponseEntity<List<MeanDtoLazy>> getAllTargets(){;
        return new ResponseEntity<>(meansDelegate.getAllTargets(), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/full/{meanid}")
    public ResponseEntity<MeanDtoFull> getFull(@PathVariable("meanid") long meanid){
        return new ResponseEntity<>(meansDelegate.getFull(meanid), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/create" , method = RequestMethod.PUT)
    public ResponseEntity<MeanDtoLazy> create(@RequestBody MeanDtoFull meanDtoFull){
        return new ResponseEntity<MeanDtoLazy>(meansDelegate.create(meanDtoFull), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/delete/{meanId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("meanId") long id){
        meansDelegate.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/update" , method = RequestMethod.POST)
    public ResponseEntity<MeanDtoFull> update(@RequestBody MeanDtoFull meanDtoFull){
        return new ResponseEntity<>(meansDelegate.update(meanDtoFull), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/reposition/list" , method = RequestMethod.POST)
    public ResponseEntity<List<MeanDtoLazy>> reposition(@RequestBody List<MeanDtoLazy> meanDtoLazyList){
        return new ResponseEntity<>(meansDelegate.reposition(meanDtoLazyList), HttpStatus.OK);
    }


}
