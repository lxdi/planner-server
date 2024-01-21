package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.SpringTestConfig;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class GracefulDeleteServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;

    @Autowired
    private IRepDAO repDAO;

    private Mean mean;
    private Layer layer;
    private Task task;
    private Topic topic;
    private TaskTesting taskTesting;
    private TaskMapper taskMapper;
    private Repetition finishedRep;
    private Repetition finishedRep2;
    private Repetition unfinishedRep;


    @BeforeEach
    public void init(){
        super.init();
    }

    private void initEntities(boolean withTaskMappers, boolean withSP, boolean withSlot){
        Session session = entityManager.unwrap(Session.class);

        mean = new Mean();
        mean.setId(UUID.randomUUID().toString());
        session.save(mean);

        layer = new Layer();
        layer.setId(UUID.randomUUID().toString());
        layer.setMean(mean);
        session.saveOrUpdate(layer);

        task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setLayer(layer);
        session.saveOrUpdate(task);

        topic = new Topic();
        topic.setId(UUID.randomUUID().toString());
        topic.setTask(task);
        session.saveOrUpdate(topic);

        taskTesting = new TaskTesting();
        taskTesting.setId(UUID.randomUUID().toString());
        taskTesting.setTask(task);
        session.saveOrUpdate(taskTesting);

        if(withTaskMappers) {

            taskMapper = new TaskMapper();
            taskMapper.setId(UUID.randomUUID().toString());
            taskMapper.setTask(task);
            session.saveOrUpdate(taskMapper);

            if(withSP) {

                finishedRep = new Repetition();
                finishedRep.setId(UUID.randomUUID().toString());
                finishedRep.setTask(task);
                session.save(finishedRep);

                finishedRep2 = new Repetition();
                finishedRep2.setId(UUID.randomUUID().toString());
                finishedRep2.setTask(task);
                session.save(finishedRep2);

                unfinishedRep = new Repetition();
                unfinishedRep.setId(UUID.randomUUID().toString());
                unfinishedRep.setTask(task);
                session.save(unfinishedRep);
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
        assertFalse(isExists(task.getId(), Task.class));
        assertFalse(isExists(topic.getId(), Topic.class));
        assertFalse(isExists(taskTesting.getId(), TaskTesting.class));
        assertFalse(isExists(taskMapper.getId(), TaskMapper.class));
        assertFalse(isExists(finishedRep.getId(), Repetition.class));

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

}
