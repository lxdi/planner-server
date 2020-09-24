package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static junit.framework.TestCase.assertFalse;

@Transactional
public class SafeDeleteServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SafeDeleteService safeDeleteService;

    private Task task;
    private Topic topic;
    private TaskTesting taskTesting;
    private TaskMapper taskMapper;
    private SpacedRepetitions spacedRepetitions;
    private Repetition repetition;


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

                repetition = new Repetition();
                repetition.setSpacedRepetitions(spacedRepetitions);
                session.saveOrUpdate(repetition);
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
        assertFalse(isExists(repetition.getId(), Repetition.class));

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

    private boolean isExists(long id, Class clazz){
        try {
            return entityManager.unwrap(Session.class).get(clazz, id) != null;
        } catch (EntityNotFoundException ex){
            return false;
        }
    }

}
