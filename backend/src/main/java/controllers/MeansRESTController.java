package controllers;

import controllers.delegates.MeansDelegate;
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
    public ResponseEntity<List<Map<String, Object>>> getAllMeans(){;
        return new ResponseEntity<>(meansDelegate.getAllMeans(), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/full/{meanid}")
    public ResponseEntity<Map<String, Object>> getFull(@PathVariable("meanid") long meanid){
        return new ResponseEntity<>(meansDelegate.getFull(meanid), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/create" , method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> meanDtoFull){
        return new ResponseEntity<>(meansDelegate.create(meanDtoFull), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/delete/{meanId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("meanId") long id){
        meansDelegate.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/update" , method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> update(@RequestBody Map<String, Object> meanDtoFull){
        return new ResponseEntity<>(meansDelegate.update(meanDtoFull), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/reposition/list" , method = RequestMethod.POST)
    public ResponseEntity<List<Map<String, Object>>> reposition(@RequestBody List<Map<String, Object>> meanDtoLazyList){
        return new ResponseEntity<>(meansDelegate.reposition(meanDtoLazyList), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/{meanid}/hideChildren/{val}" , method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> hideChildren(@PathVariable("meanid") long id, @PathVariable("val") boolean val){
        return new ResponseEntity<>(meansDelegate.hideChildren(id, val), HttpStatus.OK);
    }


}
