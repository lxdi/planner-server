package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.controllers.delegates.TargetsDelegate;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 10.03.2018.
 */
public class TargetsRESTControllerTest extends AbstractTestsWithTargets {

   @Autowired
   TargetsDelegate targetsDelegate;

   private MockMvc mockMvc;
   private TargetsRESTController targetsRESTController;

   @Before
   public void init(){
      Realm realm = new Realm("test realm");
      Target target1  = new Target("target-1", realm);
      target1.setId(1);
      Target target2 = new Target("target-1-2", realm);
      target2.setId(2);
      target2.setParent(target1);
      Target target3 = new Target("target-2", realm);
      target3.setId(3);
      List<Target> listoftargets = new ArrayList<>(Arrays.asList(target1, target2 , target3));


      targetsRESTController = new TargetsRESTController(targetsDelegate);
      mockMvc = MockMvcBuilders.standaloneSetup(targetsRESTController).build();
   }


   @Test
   public void testReceivingTopTargets() throws Exception {
      MvcResult result =  mockMvc.perform(get("/target/all/lazy")).andExpect(status().isOk()).andReturn();
      String resultStr = result.getResponse().getContentAsString();
      assertTrue(!resultStr.contains("children"));
      System.out.println(resultStr);
   }

}
