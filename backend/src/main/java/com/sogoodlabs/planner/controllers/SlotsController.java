package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IRealmDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Slot;
import com.sogoodlabs.planner.services.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/slot")
public class SlotsController {

    @Autowired
    private ISlotDAO slotDAO;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private SlotService slotService;

    @GetMapping("/all")
    public List<Map<String, Object>> getAll(){
       return slotDAO.findAll()
                .stream().map(commonMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PutMapping
    public Map<String, Object> createOne(@RequestBody Map<String, Object> dto){
        Slot slot = commonMapper.mapToEntity(dto, new Slot());
        slot.setId(UUID.randomUUID().toString());
        slotDAO.save(slot);
        return commonMapper.mapToDto(slot);
    }

    @PostMapping("/list")
    public List<Map<String, Object>> postList(@RequestBody List<Map<String, Object>> dtoList){
        return dtoList.stream()
                .map(dto -> slotService.saveOrCreate(commonMapper.mapToEntity(dto, new Slot())))
                .map(slot -> commonMapper.mapToDto(slot))
                .collect(Collectors.toList());
    }

}
