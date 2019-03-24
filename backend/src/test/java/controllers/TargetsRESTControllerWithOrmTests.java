package controllers;

import model.dto.target.TargetsDtoMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import test_configs.AbstractTestsWithTargets;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 10.03.2018.
 */

public class TargetsRESTControllerWithOrmTests extends AbstractTestsWithTargets {

    @Autowired
    TargetsDtoMapper targetsDtoMapper;

    private MockMvc mockMvc;
    private TargetsRESTController targetsRESTController;

    @Before
    public void init(){
        super.init();
        targetsRESTController = new TargetsRESTController(targetsDAO, targetsDtoMapper, realmDAO);
        mockMvc = MockMvcBuilders.standaloneSetup(targetsRESTController).build();
    }

    @Test
    public void createTest() throws Exception {
        String content = "{\"id\":0,\"title\":\"new target\",\"parentid\":2, \"realmid\":"+realm.getId()+", \"children\":[]}";
        MvcResult result = mockMvc.perform(put("/target/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();
        int newId = 5;
        assertTrue(targetsDAO.targetById(newId)!=null);
        assertTrue(targetsDAO.targetById(newId).getTitle().equals("new target"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":"+newId));
        assertTrue(result.getResponse().getContentAsString().contains("new target"));
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void deleteTest() throws Exception {
        MvcResult result = mockMvc.perform(delete("/target/delete/2"))
                .andExpect(status().isOk()).andReturn();

        assertTrue(targetsDAO.targetById(1)!=null);
        assertTrue(targetsDAO.targetById(2)==null);
        assertTrue(targetsDAO.targetById(3)!=null);
        assertTrue(targetsDAO.targetById(4)!=null);
    }

    @Test
    public void deleteParentTest() throws Exception {
        MvcResult result = mockMvc.perform(delete("/target/delete/1"))
                .andExpect(status().isOk()).andReturn();

        assertTrue(targetsDAO.targetById(1)==null);
        assertTrue(targetsDAO.targetById(2)==null);
        assertTrue(targetsDAO.targetById(3)==null);
        assertTrue(targetsDAO.targetById(4)==null);
    }

    @Test
    public void updateTest() throws Exception {
        String content = "{\"id\":2,\"title\":\"default child changed\",\"parentid\":1, \"realmid\":1}";
        MvcResult result = mockMvc.perform(post("/target/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        assertTrue(targetsDAO.targetById(2).getTitle().equals("default child changed"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":2"));
        assertTrue(result.getResponse().getContentAsString().contains("default child changed"));
        System.out.println(result.getResponse().getContentAsString());
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
