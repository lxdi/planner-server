package com.sogoodlabs.planner.util;

import com.sogoodlabs.planner.model.entities.DaysOfWeek;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTests {

    @Test
    public void getFullNameTest(){
        Task task = new Task();
        task.setTitle("task");

        String fullName = StringUtils.getFullName(task, new String[]{
                "layer?.mean?.realm?.title", "layer?.mean?.title",
                "layer?.priority", "title"
        });

        assertTrue(fullName.equals("task"));
    }

    @Test
    public void getValueTest(){

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> someDto = new HashMap<>();
        someDto.put("id", 6564L);
        list.add(someDto);

        Map<String, Object> dtowrapper = new HashMap<>();
        dtowrapper.put("listofsomething", list);

        assertEquals(6564, (long) StringUtils.getValue(dtowrapper, "get('listofsomething').get(0).get('id')"));

        Layer layer = new Layer();
        layer.setDepth(1);
        layer.setId("543543");

        Task task = new Task();
        task.setTitle("task1");
        task.setId("23");
        task.setLayer(layer);

        assertEquals("543543", StringUtils.getValue(task, "layer.id"));
    }

    @Test
    public void daysOfWeekTests(){
        assertTrue(DaysOfWeek.mon == DaysOfWeek.valueOf("mon"));
    }

}
