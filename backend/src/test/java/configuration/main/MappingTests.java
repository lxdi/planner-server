package configuration.main;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.IRepDAO;
import model.entities.Repetition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import services.DateUtils;
import test_configs.SpringTestConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class MappingTests extends SpringTestConfig {

    @Autowired
    IRepDAO repDAO;

    @Autowired
    CommonMapper commonMapper;

    @Test
    public void datesMappingTest(){
        Repetition repetition = new Repetition();
        repetition.setPlanDate(DateUtils.currentDate());
        repDAO.save(repetition);

        Map<String, Object> result = commonMapper.mapToDto(repetition, new HashMap<>());

        assertTrue(result.get("planDate").equals(DateUtils.currentDateString()));
        assertTrue(result.get("factDate")==null);
        assertTrue(!result.containsKey("factDate"));
    }

}
