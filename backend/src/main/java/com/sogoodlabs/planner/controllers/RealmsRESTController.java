package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.model.dao.IRealmDAO;
import com.sogoodlabs.planner.model.entities.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping(path = "/realm")
public class RealmsRESTController {

    @Autowired
    IRealmDAO realmDAO;

    public RealmsRESTController(){}

    public RealmsRESTController(IRealmDAO realmDAO){
        this.realmDAO = realmDAO;
    }

    @RequestMapping(path = "/all")
    public ResponseEntity<List<Realm>> getAllTargets(){
        List<Realm> result = realmDAO.getAllRealms();
        return new ResponseEntity<List<Realm>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/create" , method = RequestMethod.PUT)
    public ResponseEntity<Realm> createRealm(@RequestBody Realm realm){
        realmDAO.saveOrUpdate(realm);
        return new ResponseEntity<Realm>(realm, HttpStatus.OK);
    }

    @RequestMapping(path = "/setcurrent/{realmid}" , method = RequestMethod.POST)
    public ResponseEntity setCurrent(@PathVariable("realmid") long realmid){
        realmDAO.setCurrent(realmid);
        return new ResponseEntity(HttpStatus.OK);
    }

}