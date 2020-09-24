package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;

import java.util.*;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@Transactional
public class TasksDaoTests extends AbstractTestsWithTargets {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Override
    @Before
    public void init(){
        super.init();
    }

    @Test
    public void tasksByLayerTest(){
        Layer layer0 = new Layer();
        layerDAO.saveOrUpdate(layer0);

        Layer layer = new Layer();
        layerDAO.saveOrUpdate(layer);

        Subject subject =  new Subject();
        subject.setLayer(layer);
        subjectDAO.saveOrUpdate(subject);

        Task task1 = new Task();
        task1.setSubject(subject);
        tasksDAO.saveOrUpdate(task1);

        Task task2 = new Task();
        task2.setSubject(subject);
        tasksDAO.saveOrUpdate(task2);

        Task task3 = new Task();
        tasksDAO.saveOrUpdate(task3);

        List<Task> tasks = tasksDAO.tasksByLayer(layer);

        assertTrue(tasksDAO.tasksByLayer(layer0).size()==0);

        assertTrue(tasks.size()==2);
        assertTrue(tasks.get(0).getId()==task1.getId());
        assertTrue(tasks.get(1).getId()==task2.getId());

    }

}
