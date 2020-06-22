package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Repetition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.services.DateUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class RepDaoTests extends SpringTestConfig {

    @Autowired
    IRepDAO repDAO;

    @Test
    public void getFinishedWithPlanDateInRangeTest(){
        Repetition repetition = new Repetition();
        repetition.setPlanDay(new Day(DateUtils.toDate("2019-03-14")));
        repDAO.save(repetition);

        Repetition repetition2 = new Repetition();
        repetition2.setPlanDay(new Day(DateUtils.toDate("2019-01-10")));
        repDAO.save(repetition2);

        Repetition repetition3 = new Repetition();
        repetition3.setPlanDay(new Day(DateUtils.toDate("2019-01-10")));
        repetition3.setFactDay(new Day(DateUtils.toDate("2019-02-10")));
        repDAO.save(repetition3);

        List<Repetition> foundReps = repDAO.getUnFinishedWithPlanDateInRange(
                DateUtils.toDate("2019-03-10"),
                DateUtils.toDate("2019-03-17"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition.getId());

        foundReps = repDAO.getUnFinishedWithPlanDateInRange(
                DateUtils.toDate("2019-01-09"),
                DateUtils.toDate("2019-02-12"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition2.getId());

        foundReps = repDAO.getUnFinishedWithPlanDateInRange(
                DateUtils.toDate("2019-01-10"),
                DateUtils.toDate("2019-02-12"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition2.getId());

        foundReps = repDAO.getUnFinishedWithPlanDateInRange(
                DateUtils.toDate("2019-01-05"),
                DateUtils.toDate("2019-01-10"));
        assertTrue(foundReps.size()==1);
        assertTrue(foundReps.get(0).getId()==repetition2.getId());

        foundReps = repDAO.getUnFinishedWithPlanDateInRange(
                DateUtils.toDate("2019-01-05"),
                DateUtils.toDate("2019-04-10"));
        assertTrue(foundReps.size()==2);
        assertTrue(foundReps.get(0).getId()==repetition2.getId());
        assertTrue(foundReps.get(1).getId()==repetition.getId());

    }

}