package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.util.DateUtils;
import com.sogoodlabs.planner.util.IdUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@Transactional
public class ProgressServiceTest extends SpringTestConfig {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private IRepPlanDAO repPlanDAO;

    @Autowired
    private WeeksGenerator weeksGenerator;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private IDayDao dayDao;

    private Task task;
    private RepetitionPlan repetitionPlan;
    private RepetitionPlan dayRepetitionPlan;
    private Date finishDate;

    @Before
    public void init(){
        super.init();

        task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle("sometitle");
        tasksDAO.save(task);

        repetitionPlan = new RepetitionPlan();
        repetitionPlan.setId(UUID.randomUUID().toString());
        repetitionPlan.setPlan(new int[]{1, 2});
        repPlanDAO.save(repetitionPlan);

        dayRepetitionPlan = new RepetitionPlan();
        dayRepetitionPlan.setId(UUID.randomUUID().toString());
        dayRepetitionPlan.setPlan(new int[]{1, 2});
        dayRepetitionPlan.setDayStep(true);
        repPlanDAO.save(dayRepetitionPlan);

        finishDate = DateUtils.toDate("2021-01-11");

    }

    @Test(expected = RuntimeException.class)
    public void progressServiceTest_weeksNotGenerated(){
        progressService.finishTask(task, repetitionPlan, finishDate);
    }

    @Test
    public void progressServiceTest(){
        weeksGenerator.generateYear(2021);

        progressService.finishTask(task, repetitionPlan, finishDate);

        List<Repetition> reps = repDAO.findByTask(task);
        assertEquals("2021-01-18", DateUtils.fromDate(reps.get(0).getPlanDay().getDate()));
        assertEquals("2021-01-25", DateUtils.fromDate(reps.get(1).getPlanDay().getDate()));
    }

    @Test
    public void removeUnfinishedReps(){
        weeksGenerator.generateYear(2021);

        var unfinished1 = createRep(task, repetitionPlan, "2021-01-12", null);
        var unfinished2 = createRep(task, dayRepetitionPlan, null, null);
        var finished = createRep(task, repetitionPlan, null, "2021-01-11");

        progressService.removeUnfinishedReps(task.getId());

        cleanContext();

        assertFalse(repDAO.findById(unfinished1.getId()).isPresent());
        assertFalse(repDAO.findById(unfinished2.getId()).isPresent());
        assertTrue(repDAO.findById(finished.getId()).isPresent());

    }

    private Repetition createRep(Task task, RepetitionPlan repPlan, String planDay, String factDay){
        Repetition rep = new Repetition();
        rep.setId(UUID.randomUUID().toString());
        rep.setTask(task);
        rep.setPlanDay(planDay!=null? dayDao.findByDate(DateUtils.toDate(planDay)):null);
        rep.setFactDay(factDay!=null? dayDao.findByDate(DateUtils.toDate(factDay)):null);
        rep.setRepetitionPlan(repPlan);
        return repDAO.save(rep);
    }

}
