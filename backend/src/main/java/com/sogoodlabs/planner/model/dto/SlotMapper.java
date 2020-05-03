package com.sogoodlabs.planner.model.dto;


import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.entities.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;

@Service
@Transactional
public class SlotMapper {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISlotDAO slotDAO;

    public Map<String, Object> mapToDtoLazy(Slot slot){
        Map<String, Object> dto = commonMapper.mapToDto(slot);
        if(slot.getLayer()!=null){
            dto.put("tasksInLayer", layerDAO.taskCountInLayer(slot.getLayer()));
        }
        return dto;
    }

    public Map<String, Object> mapToDtoFull(Slot slot){
        Map<String, Object> dto = mapToDtoLazy(slot);
        dto.putIfAbsent("slotPositions", new ArrayList<>());
        slotDAO.getSlotPositionsForSlot(slot).forEach(slotPosition -> {
            ((ArrayList)dto.get("slotPositions")).add(commonMapper.mapToDto(slotPosition));
        });
        return dto;
    }

    public Slot mapToEntity(Map<String, Object> dto){
        return commonMapper.mapToEntity(dto, new Slot());
    }

}
