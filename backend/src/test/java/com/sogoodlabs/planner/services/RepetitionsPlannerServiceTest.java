package com.sogoodlabs.planner.services;


import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.ISpacedRepDAO;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;
import com.sogoodlabs.planner.util.DateUtils;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;

import static junit.framework.TestCase.*;

@Transactional
public class RepetitionsPlannerServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RepetitionsPlannerService repetitionsPlannerService;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ISpacedRepDAO spacedRepDAO;

    private RepetitionPlan repetitionPlan;
    private TaskMapper taskMapper;
    private int[] plan = new int[]{1,2,3};
    private Date finishDate = DateUtils.currentDate();

    private void initData(boolean dayStep){

        Session session = entityManager.unwrap(Session.class);

        taskMapper = new TaskMapper();
        taskMapper.setFinishDate(finishDate);
        session.save(taskMapper);

        repetitionPlan = new RepetitionPlan();
        repetitionPlan.setPlan(plan);
        repetitionPlan.setDayStep(dayStep);
        session.save(repetitionPlan);

        session.flush();
        session.clear();

    }

    @Test
    public void getOrCreateSpacedRepetitionTest_Days(){
        initData(true);

        SpacedRepetitions spacedRepetitions =  repetitionsPlannerService.getOrCreateSpacedRepetition(taskMapper, repetitionPlan.getId());

        assertEquals(taskMapper.getId(), spacedRepetitions.getTaskMapper().getId());
        assertEquals(repetitionPlan.getId(), spacedRepetitions.getRepetitionPlan().getId());

        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepetitions.getId());
        assertEquals(plan.length, repetitions.size());
        assertEquals(DateUtils.addDays(finishDate, plan[0]), repetitions.get(0).getPlanDate());
        assertEquals(DateUtils.addDays(finishDate, plan[1]), repetitions.get(1).getPlanDate());
        assertEquals(DateUtils.addDays(finishDate, plan[2]), repetitions.get(2).getPlanDate());
    }

    @Test
    public void getOrCreateSpacedRepetitionTest_Weeks(){
        initData(false);

        SpacedRepetitions spacedRepetitions =  repetitionsPlannerService.getOrCreateSpacedRepetition(taskMapper, repetitionPlan.getId());

        assertEquals(taskMapper.getId(), spacedRepetitions.getTaskMapper().getId());
        assertEquals(repetitionPlan.getId(), spacedRepetitions.getRepetitionPlan().getId());

        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepetitions.getId());
        assertEquals(plan.length, repetitions.size());
        assertEquals(DateUtils.addWeeks(finishDate, plan[0]), repetitions.get(0).getPlanDate());
        assertEquals(DateUtils.addWeeks(finishDate, plan[1]), repetitions.get(1).getPlanDate());
        assertEquals(DateUtils.addWeeks(finishDate, plan[2]), repetitions.get(2).getPlanDate());
    }

    @Test
    public void finishRepetitionTest(){
        Repetition repetition = createRep(null, null);

        repetitionsPlannerService.finishRepetition(repetition.getId());

        repetition = repDAO.getOne(repetition.getId());
        assertTrue(DateUtils.fromDate(repetition.getFactDate()).equals(DateUtils.currentDateString()));
    }

    @Test
    public void finishRepetitionWithLowingTest(){
        SpacedRepetitions spacedRepetitions = new SpacedRepetitions();
        spacedRepDAO.save(spacedRepetitions);
        Repetition repetitionBefore = createRep(spacedRepetitions, DateUtils.toDate("2000-12-04"));
        Repetition repetition = createRep(spacedRepetitions, null);
        Repetition repetitionAfter = createRep(spacedRepetitions, null);
        Repetition otherRepetition = createRep(null, null);

        repetitionsPlannerService.finishRepetitionWithLowing(repetition.getId());

        //entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

        repetitionBefore = repDAO.getOne(repetitionBefore.getId());
        repetition = repDAO.getOne(repetition.getId());
        repetitionAfter = repDAO.getOne(repetitionAfter.getId());
        otherRepetition = repDAO.getOne(otherRepetition.getId());
        assertEquals(DateUtils.fromDate(repetition.getFactDate()), DateUtils.currentDateString());
        assertFalse(otherRepetition.getIsRepetitionOnly());
        assertFalse(repetitionBefore.getIsRepetitionOnly());
        assertTrue(repetitionAfter.getIsRepetitionOnly());
    }


    private Repetition createRep(SpacedRepetitions spacedRepetitions, Date factDate){
        Repetition repetition = new Repetition();
        repetition.setSpacedRepetitions(spacedRepetitions);
        repetition.setFactDate(factDate);
        repDAO.save(repetition);
        return repetition;
    }

}
