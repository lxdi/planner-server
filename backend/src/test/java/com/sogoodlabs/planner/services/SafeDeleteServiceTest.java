package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.ISpacedRepDAO;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;
import com.sogoodlabs.planner.util.DateUtils;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

@Transactional
public class SafeDeleteServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SafeDeleteService safeDeleteService;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ISpacedRepDAO spacedRepDAO;

    private Task task;
    private Topic topic;
    private TaskTesting taskTesting;
    private TaskMapper taskMapper;
    private SpacedRepetitions spacedRepetitions;
    private Repetition finishedRep;
    private Repetition finishedRep2;
    private Repetition unfinishedRep;


    @Before
    public void init(){
        super.init();
    }

    private void initEntities(boolean withTaskMappers, boolean withSP){
        Session session = entityManager.unwrap(Session.class);

        task = new Task();
        session.saveOrUpdate(task);

        topic = new Topic();
        topic.setTask(task);
        session.saveOrUpdate(topic);

        taskTesting = new TaskTesting();
        taskTesting.setTask(task);
        session.saveOrUpdate(taskTesting);

        if(withTaskMappers) {

            taskMapper = new TaskMapper();
            taskMapper.setTask(task);
            session.saveOrUpdate(taskMapper);

            if(withSP) {
                spacedRepetitions = new SpacedRepetitions();
                spacedRepetitions.setTaskMapper(taskMapper);
                session.saveOrUpdate(spacedRepetitions);

                finishedRep = new Repetition();
                finishedRep.setSpacedRepetitions(spacedRepetitions);
                finishedRep.setPlanDate(DateUtils.currentDate());
                finishedRep.setFactDate(DateUtils.currentDate());
                session.save(finishedRep);

                finishedRep2 = new Repetition();
                finishedRep2.setSpacedRepetitions(spacedRepetitions);
                finishedRep2.setPlanDate(DateUtils.currentDate());
                finishedRep2.setFactDate(DateUtils.currentDate());
                session.save(finishedRep2);

                unfinishedRep = new Repetition();
                unfinishedRep.setSpacedRepetitions(spacedRepetitions);
                unfinishedRep.setPlanDate(DateUtils.currentDate());
                session.save(unfinishedRep);
            }
        }

        session.flush();
        session.clear();
    }

    @Test
    public void deleteTaskTest(){
        initEntities(true, true);

        safeDeleteService.deleteTask(task.getId());

        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));
        assertFalse(isExists(taskMapper.getId(), TaskMapper.class));
        assertFalse(isExists(spacedRepetitions.getId(), SpacedRepetitions.class));
        assertFalse(isExists(finishedRep.getId(), Repetition.class));

    }

    @Test
    public void deleteTaskTest_NoSpacedRepetitions(){
        initEntities(true, false);


        safeDeleteService.deleteTask(task.getId());

        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));
        assertFalse(isExists(taskMapper.getId(), TaskMapper.class));

    }

    @Test
    public void deleteTaskTest_NoTaskMapper(){
        initEntities(false, false);

        safeDeleteService.deleteTask(task.getId());

        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));

    }

    @Test
    public void removeRepetitionsLeftForTaskTest(){
        initEntities(true, true);

        safeDeleteService.removeRepetitionsLeftForTask(task.getId());

        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepDAO.getSRforTask(task.getId()).getId());
        assertEquals(2, repetitions.size());
        assertEquals(finishedRep.getId(), repetitions.get(0).getId());
        assertEquals(finishedRep2.getId(), repetitions.get(1).getId());

    }

}
