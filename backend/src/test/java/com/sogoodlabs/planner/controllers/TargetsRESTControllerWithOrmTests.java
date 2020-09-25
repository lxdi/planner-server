package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.controllers.delegates.TargetsDelegate;
import com.sogoodlabs.planner.model.entities.Target;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;

import javax.transaction.Transactional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 10.03.2018.
 */

@Transactional
public class TargetsRESTControllerWithOrmTests extends AbstractTestsWithTargets {

    @Autowired
    TargetsDelegate targetsDelegate;

    @Autowired
    private TargetsRESTController targetsRESTController;

    private MockMvc mockMvc;

    @Before
    public void init(){
        super.init();
        mockMvc = MockMvcBuilders.standaloneSetup(targetsRESTController).build();
    }

    @Test
    public void createTest() throws Exception {
        String content = "{\"id\":0,\"title\":\"new target\",\"parentid\":2, \"realmid\":"+realm.getId()+", \"children\":[]}";
        MvcResult result = mockMvc.perform(put("/target/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();
        int newId = 5;
        assertTrue(isExists(newId, Target.class));
        assertTrue(targetsDAO.getOne((long)newId).getTitle().equals("new target"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":"+newId));
        assertTrue(result.getResponse().getContentAsString().contains("new target"));
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void deleteTest() throws Exception {
        MvcResult result = mockMvc.perform(delete("/target/delete/"+target.getId()))
                .andExpect(status().isOk()).andReturn();

        assertTrue(isExists(parentTarget.getId(), Target.class));
        assertFalse(isExists(target.getId(), Target.class));
        assertTrue(isExists(target2.getId(), Target.class));
        assertTrue(isExists(target3.getId(), Target.class));
    }

    @Test
    public void deleteParentTest() throws Exception {
        MvcResult result = mockMvc.perform(delete("/target/delete/"+parentTarget.getId()))
                .andExpect(status().isOk()).andReturn();

        assertFalse(isExists(parentTarget.getId(), Target.class));
        assertFalse(isExists(target.getId(), Target.class));
        assertFalse(isExists(target2.getId(), Target.class));
        assertFalse(isExists(target3.getId(), Target.class));
    }

    @Test
    public void updateTest() throws Exception {
        String content = "{\"id\":2,\"title\":\"default child changed\",\"parentid\":1, \"realmid\":1}";
        MvcResult result = mockMvc.perform(post("/target/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        assertTrue(targetsDAO.getOne(2l).getTitle().equals("default child changed"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":2"));
        assertTrue(result.getResponse().getContentAsString().contains("default child changed"));
    }

    @Test(expected = NestedServletException.class)
    public void creatingTargetWithoutRealm() throws Exception {
        String content = "{\"id\":2,\"title\":\"default child changed\",\"parentid\":1}";
        MvcResult result = mockMvc.perform(post("/target/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        //assertTrue(result.getResponse().getContentAsString().contains("realmid is empty"));
    }



}
