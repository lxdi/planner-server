package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class TaskTestingDaoTests extends SpringTestConfig {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Test
    public void getByTaskIdTest(){

        Mean mean = new Mean();
        meansDAO.saveOrUpdate(mean);

        Layer layer = new Layer();
        layer.setMean(mean);
        layerDAO.saveOrUpdate(layer);

        Subject subject = new Subject();
        subject.setLayer(layer);
        subjectDAO.saveOrUpdate(subject);

        Task task1 = new Task();
        task1.setSubject(subject);
        tasksDAO.saveOrUpdate(task1);

        TaskTesting taskTesting1 = new TaskTesting();
        taskTesting1.setTask(task1);
        taskTestingDAO.save(taskTesting1);

        TaskTesting taskTesting2 = new TaskTesting();
        taskTesting2.setTask(task1);
        taskTestingDAO.save(taskTesting2);

        Task task2 = new Task();
        task2.setSubject(subject);
        tasksDAO.saveOrUpdate(task2);

        TaskTesting taskTesting21 = new TaskTesting();
        taskTesting21.setTask(task2);
        taskTestingDAO.save(taskTesting21);

        List<TaskTesting> testingsForTask1 = taskTestingDAO.getByTask(task1.getId());
        assertTrue(testingsForTask1.size()==2);
        assertTrue(testingsForTask1.get(0).getId()==taskTesting1.getId());
        assertTrue(testingsForTask1.get(1).getId()==taskTesting2.getId());

        List<TaskTesting> testingsForTask2 = taskTestingDAO.getByTask(task2.getId());
        assertTrue(testingsForTask2.size()==1);
        assertTrue(testingsForTask2.get(0).getId()==taskTesting21.getId());


    }
}
