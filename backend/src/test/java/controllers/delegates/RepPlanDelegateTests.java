package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import configuration.main.DefaultRepPlanCreationJob;
import model.dao.IRepPlanDAO;
import model.entities.RepetitionPlan;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test_configs.SpringTestConfig;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class RepPlanDelegateTests extends SpringTestConfig {

    @Autowired
    RepPlanDelegate repPlanDelegate;

    @Autowired
    CommonMapper commonMapper;

    @Test
    public void getAllTest(){

        List<Map<String, Object>> result = repPlanDelegate.getAll();
        RepetitionPlan repetitionPlanReceived = commonMapper.mapToEntity(result.get(0), new RepetitionPlan());

        assertTrue(result.size()==1);
        assertTrue(repetitionPlanReceived.getId() == 1);
        assertTrue(repetitionPlanReceived.getTitle().equals(DefaultRepPlanCreationJob.DEFAULT_REP_PLAN_TITLE));

    }

}
