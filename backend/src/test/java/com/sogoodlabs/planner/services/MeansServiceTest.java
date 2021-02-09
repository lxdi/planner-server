package com.sogoodlabs.planner.services;


import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ITaskTestingDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.TaskTesting;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@Transactional
public class MeansServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MeansService meansService;

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    Mean mean;
    Layer layer1;
    Task task;
    Task task2;
    TaskTesting taskTesting;
    TaskTesting taskTesting2;
    Layer layerToRemove;

    @Before
    public void init(){
        super.init();

        mean = createMean("some title mean");
        layer1 = createLayer(1, mean);
        task = createTask("some title task", layer1);
        task2 = createTask("some title task", layer1);
        taskTesting = createTesting("some question", task);
        taskTesting2 = createTesting("some question 2", task);

        layerToRemove = createLayer(2, mean);

        Session session = entityManager.unwrap(Session.class);
        session.flush();
        session.clear();
    }

    @Test
    public void modifyTest(){
        task.setTaskTestings(Arrays.asList(taskTesting));
        layer1.setTasks(Arrays.asList(task));
        mean.setLayers(Arrays.asList(layer1));

        meansService.modify(mean);

        assertEquals(1, layerDAO.findByMean(mean).size());
        assertEquals(layer1.getId(), layerDAO.findByMean(mean).get(0).getId());

        assertEquals(1, tasksDAO.findByLayer(layer1).size());
        assertEquals(task.getId(), tasksDAO.findByLayer(layer1).get(0).getId());

        assertEquals(1, taskTestingDAO.findByTask(task).size());
        assertEquals(taskTesting.getId(), taskTestingDAO.findByTask(task).get(0).getId());
    }

    private Mean createMean(String title){
        Mean mean = new Mean();
        mean.setId(UUID.randomUUID().toString());
        mean.setTitle(title);
        meansDAO.save(mean);
        return mean;
    }

    private Layer createLayer(int priority, Mean mean){
        Layer layer = new Layer();
        layer.setId(UUID.randomUUID().toString());
        layer.setPriority(priority);
        layer.setMean(mean);
        layerDAO.save(layer);
        return layer;
    }

    private Task createTask(String title, Layer layer){
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(title);
        task.setLayer(layer);
        tasksDAO.save(task);
        return task;
    }

    private TaskTesting createTesting(String question, Task task){
        TaskTesting testing = new TaskTesting();
        testing.setId(UUID.randomUUID().toString());
        testing.setQuestion(question);
        testing.setTask(task);
        taskTestingDAO.save(testing);
        return testing;
    }

}
