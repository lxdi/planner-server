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
import javax.persistence.PersistenceContext;

import java.util.List;

import static junit.framework.TestCase.*;

@Transactional
public class GracefulDeleteServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ISpacedRepDAO spacedRepDAO;

    private Mean mean;
    private Layer layer;
    private Subject subject;
    private Task task;
    private Topic topic;
    private TaskTesting taskTesting;
    private TaskMapper taskMapper;
    private SpacedRepetitions spacedRepetitions;
    private Repetition finishedRep;
    private Repetition finishedRep2;
    private Repetition unfinishedRep;
    private Slot slot;
    private SlotPosition slotPosition;


    @Before
    public void init(){
        super.init();
    }

    private void initEntities(boolean withTaskMappers, boolean withSP, boolean withSlot){
        Session session = entityManager.unwrap(Session.class);

        mean = new Mean();
        session.save(mean);

        layer = new Layer();
        layer.setMean(mean);
        session.saveOrUpdate(layer);

        subject = new Subject();
        subject.setLayer(layer);
        session.saveOrUpdate(subject);

        task = new Task();
        task.setSubject(subject);
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

            if (withSlot) {
                slot = new Slot();
                slot.setMean(mean);
                slot.setLayer(layer);
                session.saveOrUpdate(slot);

                slotPosition = new SlotPosition();
                slotPosition.setSlot(slot);
                session.saveOrUpdate(slotPosition);

                taskMapper.setSlotPosition(slotPosition);
                session.saveOrUpdate(taskMapper);
            }
        }

        session.flush();
        session.clear();
    }

    @Test
    public void deleteMeanTest(){
        initEntities(true, true, true);

        gracefulDeleteService.deleteMean(mean.getId());

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

        assertFalse(isExists(mean.getId(), Mean.class));
        assertFalse(isExists(layer.getId(), Layer.class));
        assertFalse(isExists(subject.getId(), Subject.class));
        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));
        assertFalse(isExists(taskMapper.getId(), TaskMapper.class));
        assertFalse(isExists(spacedRepetitions.getId(), SpacedRepetitions.class));
        assertFalse(isExists(finishedRep.getId(), Repetition.class));

        assertTrue(isExists(slot.getId(), Slot.class));
        assertTrue(isExists(slotPosition.getId(), SlotPosition.class));

    }

    @Test
    public void deleteTaskTest(){
        initEntities(true, true, true);

        gracefulDeleteService.deleteTask(task.getId());

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));
        assertFalse(isExists(taskMapper.getId(), TaskMapper.class));
        assertFalse(isExists(spacedRepetitions.getId(), SpacedRepetitions.class));
        assertFalse(isExists(finishedRep.getId(), Repetition.class));

    }

    @Test
    public void deleteTaskTest_NoSpacedRepetitions(){
        initEntities(true, false, true);

        gracefulDeleteService.deleteTask(task.getId());

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));
        assertFalse(isExists(taskMapper.getId(), TaskMapper.class));

    }

    @Test
    public void deleteTaskTest_NoTaskMapper(){
        initEntities(false, false, true);

        gracefulDeleteService.deleteTask(task.getId());

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));

    }

    @Test
    public void removeRepetitionsLeftForTaskTest(){
        initEntities(true, true, true);

        gracefulDeleteService.removeRepetitionsLeftForTask(task.getId());

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepDAO.getSRforTask(task.getId()).getId());
        assertEquals(2, repetitions.size());
        assertEquals(finishedRep.getId(), repetitions.get(0).getId());
        assertEquals(finishedRep2.getId(), repetitions.get(1).getId());

    }

}
