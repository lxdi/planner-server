package orm_tests;

import model.dao.IRepDAO;
import model.entities.Repetition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import services.DateUtils;
import test_configs.SpringTestConfig;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class RepDaoTests extends SpringTestConfig {

    @Autowired
    IRepDAO repDAO;

    @Test
    public void getWithPlanDateInRangeTest(){
        Repetition repetition = new Repetition();
        repetition.setPlanDate(DateUtils.toDate("2019-03-14"));
        repDAO.save(repetition);

        Repetition repetition2 = new Repetition();
        repetition2.setPlanDate(DateUtils.toDate("2019-01-10"));
        repDAO.save(repetition2);

        List<Repetition> foundReps = repDAO.getWithPlanDateInRange(
                DateUtils.toDate("2019-03-10"),
                DateUtils.toDate("2019-03-17"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition.getId());

        foundReps = repDAO.getWithPlanDateInRange(
                DateUtils.toDate("2019-01-09"),
                DateUtils.toDate("2019-02-12"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition2.getId());

        foundReps = repDAO.getWithPlanDateInRange(
                DateUtils.toDate("2019-01-10"),
                DateUtils.toDate("2019-02-12"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition2.getId());

        foundReps = repDAO.getWithPlanDateInRange(
                DateUtils.toDate("2019-01-05"),
                DateUtils.toDate("2019-01-10"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition2.getId());

        foundReps = repDAO.getWithPlanDateInRange(
                DateUtils.toDate("2019-01-05"),
                DateUtils.toDate("2019-04-10"));
        assertTrue(foundReps.size()==2);
        assertTrue(foundReps.get(0).getId()==repetition.getId());
        assertTrue(foundReps.get(1).getId()==repetition2.getId());

    }

}
