package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.planner.model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.services.StringUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;
import com.sogoodlabs.planner.test_configs.TestCreatorsAnotherSession;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class SlotMapperTest extends SpringTestConfig {

    @Autowired
    TestCreatorsAnotherSession testCreators;

    @Autowired
    SlotMapper slotMapper;

    @Test
    public void mapToDtoLazyTest(){
        Layer layer = testCreators.createLayer(null);
        Subject subject = testCreators.createSubject(layer);
        Task task = testCreators.createTask(subject);
        Task task2 = testCreators.createTask(subject);

        Slot slot = testCreators.createSlot(layer, null);

        Map<String, Object> dto = slotMapper.mapToDtoLazy(slot);

        assertTrue((long)dto.get("tasksInLayer")==2);

    }

    @Test
    public void mapToDtoFullTest(){
        Layer layer = testCreators.createLayer(null);
        Subject subject = testCreators.createSubject(layer);
        Task task = testCreators.createTask(subject);
        Task task2 = testCreators.createTask(subject);

        Slot slot = testCreators.createSlot(layer, null);

        SlotPosition slotPosition = testCreators.createSlotPosition(slot);
        slotPosition.setDayOfWeek(DaysOfWeek.thu);

        SlotPosition slotPosition2 = testCreators.createSlotPosition(slot);
        slotPosition2.setDayOfWeek(DaysOfWeek.mon);

        SlotPosition slotPosition3 = testCreators.createSlotPosition(slot);
        slotPosition3.setDayOfWeek(DaysOfWeek.fri);

        testCreators.save(slotPosition, slotPosition2, slotPosition3);

        Map<String, Object> dto = slotMapper.mapToDtoFull(slot);

        assertTrue((long)dto.get("tasksInLayer")==2);
        assertTrue((int) StringUtils.getValue(dto, "get('slotPositions').size()") ==3);
        assertTrue((long) StringUtils.getValue(dto, "get('slotPositions').get(0).get('id')")==slotPosition.getId());
        assertTrue((long) StringUtils.getValue(dto, "get('slotPositions').get(1).get('id')")==slotPosition2.getId());
        assertTrue((long) StringUtils.getValue(dto, "get('slotPositions').get(2).get('id')")==slotPosition3.getId());

        assertTrue(StringUtils.getValue(dto, "get('slotPositions').get(0).get('dayOfWeek')").equals("thu"));

    }

}
