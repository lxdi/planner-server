package controllers;

import model.ITargetsDAO;
import model.dto.target.TargetDtoWithParentLazy;
import model.dto.target.TargetsDtoMapper;
import model.entities.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 23.02.2018.
 */

@Controller
public class TargetsRESTController {

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    TargetsDtoMapper targetsDtoMapper;

    public TargetsRESTController(){}

    public TargetsRESTController(ITargetsDAO targetsDAO, TargetsDtoMapper targetsDtoMapper){
        this.targetsDAO = targetsDAO;
        this.targetsDtoMapper = targetsDtoMapper;
    }

    @RequestMapping(path = "/target/all/lazy")
    public ResponseEntity<List<TargetDtoWithParentLazy>> getAllTargets(){
        List<TargetDtoWithParentLazy> result = new ArrayList<>();
        targetsDAO.allTargets().forEach(t -> result.add(targetsDtoMapper.targetDtoWithParentLazy(t)));
        return new ResponseEntity<List<TargetDtoWithParentLazy>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/target/create" , method = RequestMethod.PUT)
    public ResponseEntity<TargetDtoWithParentLazy> createTarget(@RequestBody TargetDtoWithParentLazy targetDto){
        Target target = targetsDtoMapper.targetFromDtoWithParentLazy(targetDto);
        targetsDAO.saveOrUpdate(target);
        return new ResponseEntity<TargetDtoWithParentLazy>(targetsDtoMapper.targetDtoWithParentLazy(target), HttpStatus.OK);
    }

    @RequestMapping(path = "/target/delete/{targetId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("targetId") long id){
        try {
            targetsDAO.deleteTarget(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/target/update" , method = RequestMethod.POST)
    public ResponseEntity<TargetDtoWithParentLazy> update(@RequestBody TargetDtoWithParentLazy targetDto){
        Target target = targetsDtoMapper.targetFromDtoWithParentLazy(targetDto);
        targetsDAO.saveOrUpdate(target);
        return new ResponseEntity<TargetDtoWithParentLazy>(targetsDtoMapper.targetDtoWithParentLazy(target), HttpStatus.OK);
    }

}
