package model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Week;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static services.DateUtils.toDate;

public class WeekMapperTests {

    @Test
    public void datesMappingTest() throws JsonProcessingException {

        Week week = new Week(toDate("2014-12-24"), null, 1);

        ObjectMapper mapper = new ObjectMapper();

        String jsonInString = mapper.writeValueAsString(week);

        assertTrue(jsonInString.contains("2014-12-24"));
        assertTrue(jsonInString.equals("{\"id\":0,\"startDay\":\"2014-12-24\",\"endDay\":null,\"number\":1}"));
    }

}
