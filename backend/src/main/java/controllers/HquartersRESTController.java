package controllers;

import controllers.delegates.HquartersDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/hquarter")
@Transactional
public class HquartersRESTController {


    @Autowired
    HquartersDelegate hquartersDelegate;

    public HquartersRESTController(){}

    public HquartersRESTController(HquartersDelegate hquartersDelegate){
        this.hquartersDelegate = hquartersDelegate;
    }

    @RequestMapping(path = "/all")
    public ResponseEntity<List<Map<String, Object>>> getAllQuarters(){
        return new ResponseEntity<>(hquartersDelegate.getAllQuarters(), HttpStatus.OK);
    }

    @RequestMapping(path = "/currentlist")
    public ResponseEntity<List<Map<String, Object>>> getCurrentList(){
        return new ResponseEntity<>(hquartersDelegate.getCurrentHquarters(), HttpStatus.OK);
    }

    @RequestMapping(path = "/currentlist/full")
    public ResponseEntity<List<Map<String, Object>>> getCurrentListFull(){
        return new ResponseEntity<>(hquartersDelegate.getCurrentHquartersFull(), HttpStatus.OK);
    }

    @RequestMapping(path = "/currentlist/full/currentyear")
    public ResponseEntity<List<Map<String, Object>>> getHquartersFullCurrentYear(){
        return new ResponseEntity<>(hquartersDelegate.getHquartersFullCurrentYear(), HttpStatus.OK);
    }

    @RequestMapping(path = "/prev/{hquarterid}/{count}")
    public ResponseEntity<List<Map<String, Object>>> getPrev(
            @PathVariable("hquarterid") long hqid, @PathVariable("count") int count){
        return new ResponseEntity<>(hquartersDelegate.getPrev(hqid, count), HttpStatus.OK);
    }

    @RequestMapping(path = "/next/{hquarterid}/{count}")
    public ResponseEntity<List<Map<String, Object>>> getNext(
            @PathVariable("hquarterid") long hqid, @PathVariable("count") int count){
        return new ResponseEntity<>(hquartersDelegate.getNext(hqid, count), HttpStatus.OK);
    }

    @RequestMapping(path="/get/{hquarterid}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> get(@PathVariable("hquarterid") long hqid){
        return new ResponseEntity<>(hquartersDelegate.get(hqid), HttpStatus.OK);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> update(@RequestBody Map<String, Object> hquarterDto){
        return new ResponseEntity<>(hquartersDelegate.update(hquarterDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/assignmean/{meanid}/toslot/{slotid}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> assign(@PathVariable("meanid") long meanid, @PathVariable("slotid") long slotid){
        return new ResponseEntity<>(hquartersDelegate.assign(meanid, slotid), HttpStatus.OK);
    }

    @RequestMapping(path="/slot/unassign/{slotid}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> unassign(@PathVariable("slotid") long slotid){
        return new ResponseEntity<>(hquartersDelegate.unassign(slotid), HttpStatus.OK);
    }

    @RequestMapping(path="/get/default", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getDefault(){
        return new ResponseEntity<>(hquartersDelegate.getDefault(), HttpStatus.OK);
    }

    @RequestMapping(path="/set/default", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setDefault(@RequestBody Map<String, Object> hquarterDtoLazy){
        return new ResponseEntity<>(hquartersDelegate.setDefault(hquarterDtoLazy), HttpStatus.OK);
    }

    @RequestMapping(path="/push/tasks/week/{weekid}/on/day/{dayOfWeekShort}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> pushTask(@PathVariable("weekid") long weekid, @PathVariable("dayOfWeekShort") String dayOfWeekShort){
        hquartersDelegate.pushTasks(weekid, dayOfWeekShort);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
