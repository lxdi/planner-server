package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.services.SpacedRepetitionsService;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.util.DateUtils;
import com.sogoodlabs.planner.services.WeeksGenerator;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Transactional
public class SpacedRepetitionsServiceTests extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    SpacedRepetitionsService spacedRepetitionsService;

    @Autowired
    WeeksGenerator weeksGenerator;

    @Autowired
    IWeekDAO weekDAO;

    @Test
    public void getActualTaskToRepeatTest(){

        Session session = entityManager.unwrap(Session.class);

        Task task1 = initEntChain(DateUtils.currentDate());
        Task task2 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), 2));
        Task task3 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), -1));
        Task task4 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), -5));
        Task task5 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), 6));

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        weeksGenerator.generateYear(currentYear);
        Week currentWeek = weekDAO.weekOfDate(DateUtils.currentDate());
        DaysOfWeek currentDayOfWeek = DaysOfWeek.findById(DateUtils.differenceInDays(currentWeek.getStartDay(), DateUtils.currentDate()));

        Task currentTask = new Task();
        session.save(currentTask);

        SlotPosition slotPosition = new SlotPosition();
        slotPosition.setDayOfWeek(currentDayOfWeek);
        session.save(slotPosition);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setWeek(currentWeek);
        taskMapper.setSlotPosition(slotPosition);
        taskMapper.setTask(currentTask);
        session.save(taskMapper);

        RepetitionPlan repetitionPlan = new RepetitionPlan();
        session.save(repetitionPlan);

        SpacedRepetitions spacedRepetitions = new SpacedRepetitions();
        spacedRepetitions.setTaskMapper(taskMapper);
        spacedRepetitions.setRepetitionPlan(repetitionPlan);
        session.save(spacedRepetitions);

        Topic topicForTask5 = new Topic();
        topicForTask5.setTask(task5);
        session.save(topicForTask5);

        Repetition repetitionDone = new Repetition();
        repetitionDone.setSpacedRepetitions(spacedRepetitions);
        repetitionDone.setPlanDate(DateUtils.addDays(DateUtils.currentDate(), 3));
        repetitionDone.setFactDate(DateUtils.addDays(DateUtils.currentDate(), 4));
        session.save(repetitionDone);

        Map<Integer, List<Map<String, Object>>> tasks = spacedRepetitionsService.getActualTaskToRepeat();

        assertTrue(tasks.get(-1).size()==1);
        assertTrue((long)tasks.get(-1).get(0).get("id")==task4.getId());

        assertTrue(tasks.get(0).size()==3);
        assertTrue((long)tasks.get(0).get(0).get("id")==task3.getId());
        assertTrue((long)tasks.get(0).get(1).get("id")==task1.getId());
        assertTrue((long)tasks.get(0).get(2).get("id")==task2.getId());

        assertTrue(tasks.get(1).size()==1);
        assertTrue((long)tasks.get(1).get(0).get("id")==task5.getId());
        assertTrue(((List)tasks.get(1).get(0).get("topics")).size()==1);
        assertTrue((long)((Map)((List)tasks.get(1).get(0).get("topics")).get(0)).get("id")==topicForTask5.getId());

        assertTrue(tasks.get(100).size()==1);
        assertTrue((long)tasks.get(100).get(0).get("id")==currentTask.getId());
    }

    private Task initEntChain(Date planDate){
        Session session = entityManager.unwrap(Session.class);

        Task task = new Task();
        session.save(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        session.save(taskMapper);

        RepetitionPlan repetitionPlan = new RepetitionPlan();
        session.save(repetitionPlan);

        SpacedRepetitions spacedRepetitions = new SpacedRepetitions();
        spacedRepetitions.setTaskMapper(taskMapper);
        spacedRepetitions.setRepetitionPlan(repetitionPlan);
        session.save(spacedRepetitions);

        Repetition repetition = new Repetition();
        repetition.setSpacedRepetitions(spacedRepetitions);
        repetition.setPlanDate(planDate);
        session.save(repetition);

        return task;
    }

}
