package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IRealmDAO;
import com.sogoodlabs.planner.model.entities.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/realms")
public class RealmsRESTController {

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    private CommonMapper commonMapper;

    public RealmsRESTController(){}

    public RealmsRESTController(IRealmDAO realmDAO){
        this.realmDAO = realmDAO;
    }

    @GetMapping("/all")
    public List<Map<String, Object>> getAllTargets(){
       return realmDAO.findAll()
                .stream().map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PutMapping
    public Map<String, Object> createRealm(@RequestBody Map<String, Object> realmDto){
        Realm realm = commonMapper.mapToEntity(realmDto, new Realm());
        realm.setId(UUID.randomUUID().toString());
        realmDAO.save(realm);
        return commonMapper.mapToDto(realm);
    }

    @PostMapping("/current/{realmid}")
    public void setCurrent(@PathVariable("realmid") String realmId){
        Realm realm = realmDAO.findById(realmId).orElseThrow(() -> new RuntimeException("Realm not found by " + realmId));
        realmDAO.clearCurrent(realm);
        realm.setCurrent(true);
        realmDAO.save(realm);
    }

}
