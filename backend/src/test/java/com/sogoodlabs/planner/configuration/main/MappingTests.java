package com.sogoodlabs.planner.configuration.main;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Repetition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.services.DateUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class MappingTests extends SpringTestConfig {

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    IDayDao dayDao;

    @Test
    public void datesMappingTest(){
        Day day = new Day(DateUtils.currentDate());
        dayDao.save(day);

        Repetition repetition = new Repetition();
        repetition.setPlanDay(day);
        repDAO.save(repetition);

        Map<String, Object> result = commonMapper.mapToDto(repetition, new HashMap<>());

        assertTrue(result.get("planDate").equals(DateUtils.currentDateString()));
        assertTrue(result.get("factDate")==null);
        assertTrue(!result.containsKey("factDate"));
    }

}
