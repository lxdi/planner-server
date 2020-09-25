package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.controllers.delegates.MeansDelegate;
import com.sogoodlabs.planner.services.TaskMappersService;
import com.sogoodlabs.planner.model.entities.Mean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.ATestsWithTargetsMeansQuartalsGenerated;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 08.04.2018.
 */

@Transactional
public class MeansRESTController_Delete_Tests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    TaskMappersService taskMappersController;

    @Autowired
    MeansDelegate meansDelegate;

    private MockMvc mockMvc;
    private MeansRESTController meansRESTController;

    @Before
    public void init(){
        super.init();
        meansRESTController = new MeansRESTController(meansDelegate);
        mockMvc = MockMvcBuilders.standaloneSetup(meansRESTController).build();
    }

    @Test
    public void deletingTest() throws Exception {
        Mean lastMean = new Mean("last mean", realm);
        meansDao.save(lastMean);

        Mean middleMean = new Mean("middle mean", realm);
        middleMean.setNext(lastMean);
        meansDao.save(middleMean);

        Mean firstMean = new Mean("first mean", realm);
        firstMean.setNext(middleMean);
        meansDao.save(firstMean);

        MvcResult result = mockMvc.perform(delete("/mean/delete/"+middleMean.getId()))
                .andExpect(status().isOk()).andReturn();

        firstMean = meansDao.getOne(firstMean.getId());
        assertTrue(firstMean.getNext().getId()==lastMean.getId());

        assertFalse(isExists(middleMean.getId(), Mean.class));

    }

    @Test
    public void deletingWithChildrenTest() throws Exception {
        Mean parentMean = new Mean("test parent mean", realm);
        meansDao.save(parentMean);

        Mean child1 = new Mean("child 1 mean", realm);
        child1.setParent(parentMean);
        meansDao.save(child1);

        Mean child2 = new Mean("child 2 mean", realm);
        child2.setNext(child1);
        child2.setParent(parentMean);
        meansDao.save(child2);

        Mean child3 = new Mean("child 3 mean", realm);
        child3.setNext(child2);
        child3.setParent(parentMean);
        meansDao.save(child3);

        Mean childChild = new Mean("childChild 1 mean", realm);
        childChild.setParent(child2);
        meansDao.save(childChild);

        MvcResult result = mockMvc.perform(delete("/mean/delete/"+parentMean.getId()))
                .andExpect(status().isOk()).andReturn();

        assertFalse(isExists(parentMean.getId(), Mean.class));
        assertFalse(isExists(child1.getId(), Mean.class));
        assertFalse(isExists(child2.getId(), Mean.class));
        assertFalse(isExists(child3.getId(), Mean.class));
        assertFalse(isExists(childChild.getId(), Mean.class));

    }



}
