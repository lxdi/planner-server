package controllers;

import controllers.delegates.HquartersDelegate;
import model.dao.*;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotMapper;
import model.dto.slot.SlotPositionDtoLazy;
import model.dto.slot.SlotPositionMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

@Controller
@RequestMapping(path = "/hquarter")
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

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseEntity<HquarterDtoLazy> update(@RequestBody HquarterDtoLazy hquarterDto){
        return new ResponseEntity<HquarterDtoLazy>(hquartersDelegate.update(hquarterDto), HttpStatus.OK);
    }

    @RequestMapping(path = "/assignmean/{meanid}/toslot/{slotid}", method = RequestMethod.POST)
    public ResponseEntity<SlotDtoLazy> assign(@PathVariable("meanid") long meanid, @PathVariable("slotid") long slotid){
        return new ResponseEntity<>(hquartersDelegate.assign(meanid, slotid), HttpStatus.OK);
    }

    @RequestMapping(path="/slot/unassign/{slotid}", method = RequestMethod.POST)
    public ResponseEntity<SlotDtoLazy> unassign(@PathVariable("slotid") long slotid){
        return new ResponseEntity<>(hquartersDelegate.unassign(slotid), HttpStatus.OK);
    }

    @RequestMapping(path="/get/default", method = RequestMethod.GET)
    public ResponseEntity<HquarterDtoLazy> getDefault(){
        return new ResponseEntity<>(hquartersDelegate.getDefault(), HttpStatus.OK);
    }

    @RequestMapping(path="/set/default", method = RequestMethod.POST)
    public ResponseEntity<HquarterDtoLazy> setDefault(@RequestBody HquarterDtoLazy hquarterDtoLazy){
        return new ResponseEntity<>(hquartersDelegate.setDefault(hquarterDtoLazy), HttpStatus.OK);
    }

}
