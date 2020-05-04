package com.sogoodlabs.planner.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogoodlabs.planner.model.entities.Week;
import org.junit.Test;

import java.util.TimeZone;

import static junit.framework.TestCase.assertTrue;
import static com.sogoodlabs.planner.services.DateUtils.toDate;

public class WeekMapperTests {

    @Test
    public void datesMappingTest() throws JsonProcessingException {

        Week week = new Week(toDate("2014-12-24"), null, 1);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());

        String jsonInString = mapper.writeValueAsString(week);

        assertTrue(jsonInString.contains("2014-12-24"));
        assertTrue(jsonInString.equals("{\"id\":0,\"startDay\":\"2014-12-24\",\"endDay\":null,\"number\":1}"));
    }

}
