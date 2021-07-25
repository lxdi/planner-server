package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.controllers.dto.MovingPlansDto;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@Transactional
public class MoveRepetitionsServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MoveRepetitionsService moveRepetitionsService;

    @Autowired
    private WeekService weekService;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private IRepPlanDAO repPlanDAO;

    @Test
    public void moveTest_forward(){
        moveTest("2021-07-28", "2021-07-28", "2021-07-31");
    }

    @Test
    public void moveTest_backward(){
        moveTest("2021-07-24", "2021-07-24", "2021-07-27");
    }

    private void moveTest(String targetDate, String rep1planExpected, String rep2planExpected){

        Task task = createTask();
        RepetitionPlan repetitionPlan = createRepPlan();

        Date planDate = DateUtils.toDate("2021-07-26");
        List<Week> weekList = weekService.getWeeksOnDate(planDate); //generate days and weeks
        Repetition repetition = createRep(dayDao.findByDate(planDate), task, repetitionPlan);

        Date planDate2 = DateUtils.toDate("2021-07-29");
        Repetition repetition2 = createRep(dayDao.findByDate(planDate2), task, repetitionPlan);

        Date planDate0 = DateUtils.toDate("2021-07-24");
        Date factDate0 = DateUtils.toDate("2021-07-25");

        Repetition repetition0 = createRep(
                dayDao.findByDate(planDate0), dayDao.findByDate(factDate0), task, repetitionPlan);

        moveRepetitionsService.move(Arrays.asList(repetition), dayDao.findByDate(DateUtils.toDate(targetDate)));

        Session session = entityManager.unwrap(Session.class);
        session.flush();
        session.clear();

        repetition = repDAO.findById(repetition.getId()).get();
        repetition2 = repDAO.findById(repetition2.getId()).get();
        repetition0 = repDAO.findById(repetition0.getId()).get();
        assertEquals(rep1planExpected, DateUtils.fromDate(repetition.getPlanDay().getDate()));
        assertEquals(rep2planExpected, DateUtils.fromDate(repetition2.getPlanDay().getDate()));

        assertEquals("2021-07-24", DateUtils.fromDate(repetition0.getPlanDay().getDate()));
        assertEquals("2021-07-25", DateUtils.fromDate(repetition0.getFactDay().getDate()));


    }

    private Repetition createRep(Day planDay, Task task, RepetitionPlan repPlan){
        return createRep(planDay, null, task, repPlan);
    }

    private Repetition createRep(Day planDay, Day factDay, Task task, RepetitionPlan repPlan){
        Repetition repetition = new Repetition();
        repetition.setId(UUID.randomUUID().toString());
        repetition.setPlanDay(planDay);
        repetition.setTask(task);
        repetition.setFactDay(factDay);
        repetition.setRepetitionPlan(repPlan);
        repDAO.save(repetition);
        return repetition;
    }

    private Task createTask(){
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        tasksDAO.save(task);
        return task;
    }

    private RepetitionPlan createRepPlan(){
        RepetitionPlan repetitionPlan = new RepetitionPlan();
        repetitionPlan.setId(UUID.randomUUID().toString());
        repPlanDAO.save(repetitionPlan);
        return repetitionPlan;
    }

}
