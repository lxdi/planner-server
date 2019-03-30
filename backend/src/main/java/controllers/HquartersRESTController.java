package controllers;

import controllers.delegates.HquartersDelegate;
import model.dto.hquarter.HquarterDtoFull;
import model.dto.hquarter.HquarterDtoLazy;
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
    public ResponseEntity<List<HquarterDtoLazy>> getAllQuarters(){
        return new ResponseEntity<List<HquarterDtoLazy>>(hquartersDelegate.getAllQuarters(), HttpStatus.OK);
    }

    @RequestMapping(path = "/currentlist")
    public ResponseEntity<List<HquarterDtoLazy>> getCurrentList(){
        return new ResponseEntity<List<HquarterDtoLazy>>(hquartersDelegate.getCurrentHquarters(), HttpStatus.OK);
    }

    @RequestMapping(path = "/currentlist/full")
    public ResponseEntity<List<HquarterDtoFull>> getCurrentListFull(){
        return new ResponseEntity<List<HquarterDtoFull>>(hquartersDelegate.getCurrentHquartersFull(), HttpStatus.OK);
    }

    @RequestMapping(path = "/currentlist/full/currentyear")
    public ResponseEntity<List<HquarterDtoFull>> getHquartersFullCurrentYear(){
        return new ResponseEntity<List<HquarterDtoFull>>(hquartersDelegate.getHquartersFullCurrentYear(), HttpStatus.OK);
    }

    @RequestMapping(path = "/prev/{hquarterid}/{count}")
    public ResponseEntity<List<HquarterDtoLazy>> getPrev(
            @PathVariable("hquarterid") long hqid, @PathVariable("count") int count){
        return new ResponseEntity<List<HquarterDtoLazy>>(hquartersDelegate.getPrev(hqid, count), HttpStatus.OK);
    }

    @RequestMapping(path = "/next/{hquarterid}/{count}")
    public ResponseEntity<List<HquarterDtoLazy>> getNext(
            @PathVariable("hquarterid") long hqid, @PathVariable("count") int count){
        return new ResponseEntity<List<HquarterDtoLazy>>(hquartersDelegate.getNext(hqid, count), HttpStatus.OK);
    }

    @RequestMapping(path="/get/{hquarterid}", method = RequestMethod.GET)
    public ResponseEntity<HquarterDtoFull> get(@PathVariable("hquarterid") long hqid){
        return new ResponseEntity<>(hquartersDelegate.get(hqid), HttpStatus.OK);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseEntity<HquarterDtoFull> update(@RequestBody HquarterDtoFull hquarterDto){
        return new ResponseEntity<HquarterDtoFull>(hquartersDelegate.update(hquarterDto), HttpStatus.OK);
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
    public ResponseEntity<HquarterDtoFull> getDefault(){
        return new ResponseEntity<>(hquartersDelegate.getDefault(), HttpStatus.OK);
    }

    @RequestMapping(path="/set/default", method = RequestMethod.POST)
    public ResponseEntity<HquarterDtoFull> setDefault(@RequestBody HquarterDtoFull hquarterDtoLazy){
        return new ResponseEntity<>(hquartersDelegate.setDefault(hquarterDtoLazy), HttpStatus.OK);
    }

}
