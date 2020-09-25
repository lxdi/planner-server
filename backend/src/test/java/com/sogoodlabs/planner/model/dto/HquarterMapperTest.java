package com.sogoodlabs.planner.model.dto;

import com.sogoodlabs.planner.model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.util.DateUtils;
import com.sogoodlabs.planner.util.StringUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;
import com.sogoodlabs.planner.test_configs.TestCreatorsAnotherSession;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class HquarterMapperTest extends SpringTestConfig {

    @Autowired
    TestCreatorsAnotherSession testCreators;

    @Autowired
    HquarterMapper hquarterMapper;

    @Test
    public void mapToLazyTest(){
        Week startWeek = testCreators.createWeek(
                DateUtils.toDate("2019-06-01"),
                DateUtils.toDate("2019-06-07"));

        Week endWeek = testCreators.createWeek(
                DateUtils.toDate("2019-06-22"),
                DateUtils.toDate("2019-06-28"));

        HQuarter hQuarter = testCreators.createHquarter(startWeek, endWeek);

        Slot slot = testCreators.createSlot(null, null, hQuarter);
        Slot slot2 = testCreators.createSlot(null, null, hQuarter);

        Map<String, Object> result = hquarterMapper.mapToDtoLazy(hQuarter);

        assertTrue((long)StringUtils.getValue(result, "get('startWeek').get('id')")==startWeek.getId());
        assertTrue((long)StringUtils.getValue(result, "get('endWeek').get('id')")==endWeek.getId());

        assertTrue((int)StringUtils.getValue(result, "get('slotsLazy').size()")==2);
    }

    @Test
    public void mapToDtoFullTest(){
        Week startWeek = testCreators.createWeek("2019-07-01","2019-07-07");
        Week week2 = testCreators.createWeek("2019-07-08","2019-07-14");
        Week week3 = testCreators.createWeek("2019-07-15","2019-07-21");
        Week endWeek = testCreators.createWeek("2019-07-22", "2019-07-28");

        HQuarter hQuarter = testCreators.createHquarter(startWeek, endWeek);

        Slot slot = testCreators.createSlot(null, null, hQuarter);
        Slot slot2 = testCreators.createSlot(null, null, hQuarter);

        SlotPosition slotPosition1 = testCreators.createSlotPosition(slot, DaysOfWeek.mon, 1);
        SlotPosition slotPosition2 = testCreators.createSlotPosition(slot, DaysOfWeek.fri, 2);
        SlotPosition slotPosition3 = testCreators.createSlotPosition(slot, DaysOfWeek.sun, 1);


        Mean mean = testCreators.createMean(testCreators.createRealm());
        Layer layer = testCreators.createLayer(mean);
        Subject subject = testCreators.createSubject(layer);

        Task task = testCreators.createTask(subject);
        TaskMapper taskMapper = testCreators.createTaskMapper(task, startWeek, slotPosition1);

        Task task2 = testCreators.createTask(subject);
        TaskMapper taskMapper2 = testCreators.createTaskMapper(task2, week2, slotPosition1);

        Task task3 = testCreators.createTask(subject);
        TaskMapper taskMapper3 = testCreators.createTaskMapper(task3, week2, slotPosition2);

        Map<String, Object> result = hquarterMapper.mapToDtoFull(hQuarter);

        assertTrue((long)StringUtils.getValue(result, "get('startWeek').get('id')")==startWeek.getId());
        assertTrue((long)StringUtils.getValue(result, "get('endWeek').get('id')")==endWeek.getId());

        assertTrue((int)StringUtils.getValue(result, "get('slotsLazy').size()")==2);

        assertTrue((int)StringUtils.getValue(result, "get('slots').size()")==2);
        assertTrue((long)StringUtils.getValue(result,
                "get('slots').get(0).get('slotPositions').get(0).get('id')")==slotPosition1.getId());
        assertTrue((long)StringUtils.getValue(result,
                "get('slots').get(0).get('slotPositions').get(1).get('id')")==slotPosition2.getId());

        assertTrue(StringUtils.getValue(result,
                "get('weeks').get(0).get('startDay')").equals("2019-07-01"));
        assertTrue((long)StringUtils.getValue(result,
                "get('weeks').get(0).get('days').get('mon').get(0).get('id')")==task.getId());

        assertTrue(StringUtils.getValue(result,
                "get('weeks').get(1).get('startDay')").equals("2019-07-08"));
        assertTrue((long)StringUtils.getValue(result,
                "get('weeks').get(1).get('days').get('mon').get(0).get('id')")==task2.getId());
        assertTrue((long)StringUtils.getValue(result,
                "get('weeks').get(1).get('days').get('fri').get(0).get('id')")==task3.getId());
    }

    @Test
    public void mapToEntityTest(){
        Week startWeek = testCreators.createWeek("2019-07-08", "2019-07-10");

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", 45);

        Map<String, Object> starWeek = new HashMap<>();
        starWeek.put("id", startWeek.getId());
        starWeek.put("startDay", "some Date");
        dto.put("startWeek", starWeek);

        HQuarter hQuarter = hquarterMapper.mapToEntity(dto);

        assertTrue(hQuarter.getStartWeek()!=null);
        assertTrue(DateUtils.fromDate(hQuarter.getStartWeek().getStartDay()).equals("2019-07-08"));

    }

}
