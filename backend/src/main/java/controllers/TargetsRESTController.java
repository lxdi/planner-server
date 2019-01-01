package controllers;

import model.dao.IRealmDAO;
import model.dao.ITargetsDAO;
import model.dto.mean.MeanDtoLazy;
import model.dto.target.TargetDtoLazy;
import model.dto.target.TargetsDtoMapper;
import model.entities.Mean;
import model.entities.Realm;
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

    @Autowired
    IRealmDAO realmDAO;

    public TargetsRESTController(){}

    public TargetsRESTController(ITargetsDAO targetsDAO, TargetsDtoMapper targetsDtoMapper, IRealmDAO realmDAO){
        this.targetsDAO = targetsDAO;
        this.targetsDtoMapper = targetsDtoMapper;
        this.realmDAO = realmDAO;
    }

    @RequestMapping(path = "/target/all/lazy")
    public ResponseEntity<List<TargetDtoLazy>> getAllTargets(){
        List<TargetDtoLazy> result = new ArrayList<>();
        targetsDAO.allTargets().forEach(t -> result.add(targetsDtoMapper.mapToDto(t)));
        return new ResponseEntity<List<TargetDtoLazy>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/target/create" , method = RequestMethod.PUT)
    public ResponseEntity<TargetDtoLazy> createTarget(@RequestBody TargetDtoLazy targetDto) {
        if(!(targetDto.getId()==0 && targetDto.getNextid()==null && targetDto.getRealmid()>0)){
            throw new RuntimeException("Not valid Target Dto received to create");
        }
        Realm realm = realmDAO.realmById(targetDto.getRealmid());
        Target target = targetsDtoMapper.mapToEntity(targetDto);
        Target prevTarget = targetsDAO.getLastOfChildren(target.getParent(), realm);
        targetsDAO.saveOrUpdate(target);
        if(prevTarget!=null){
            prevTarget.setNext(target);
            targetsDAO.saveOrUpdate(prevTarget);
        }
        return new ResponseEntity<TargetDtoLazy>(targetsDtoMapper.mapToDto(target), HttpStatus.OK);
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
    public ResponseEntity<TargetDtoLazy> update(@RequestBody TargetDtoLazy targetDto) {
        Target target = targetsDtoMapper.mapToEntity(targetDto);
        targetsDAO.saveOrUpdate(target);
        return new ResponseEntity<TargetDtoLazy>(targetsDtoMapper.mapToDto(target), HttpStatus.OK);
    }

    @RequestMapping(path = "/target/update/list" , method = RequestMethod.POST)
    public ResponseEntity<List<TargetDtoLazy>> updateList(@RequestBody List<TargetDtoLazy> targetDtoLazies){
        List<Target> updated = new ArrayList<>();
        for(TargetDtoLazy targetDtoLazy : targetDtoLazies) {
            Target target = targetsDtoMapper.mapToEntity(targetDtoLazy);
            targetsDAO.saveOrUpdate(target);
            updated.add(target);
        }
        List<TargetDtoLazy> result = new ArrayList<>();
        for(Target target: updated) {
            result.add(targetsDtoMapper.mapToDto(target));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
