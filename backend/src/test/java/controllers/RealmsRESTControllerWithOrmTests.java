package controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import test_configs.AbstractTestsWithTargets;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RealmsRESTControllerWithOrmTests extends AbstractTestsWithTargets {


    private MockMvc mockMvc;
    private RealmsRESTController realmsRESTController;

    @Before
    public void init(){
        super.init();
        realmsRESTController = new RealmsRESTController(realmDAO);
        mockMvc = MockMvcBuilders.standaloneSetup(realmsRESTController).build();
    }

    @Test
    public void allRealmsTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/realm/all"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("\"test realm\""));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":1"));
    }

    @Test
    public void createRealmTest() throws Exception {
        String content = "{\"id\":0,\"title\":\"new realm\"}";
        MvcResult result = mockMvc.perform(put("/realm/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();
        int newId = 2;
        assertTrue(realmDAO.realmById(newId) != null);
        assertTrue(realmDAO.realmById(newId).getTitle().equals("new realm"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":" + newId));
        assertTrue(result.getResponse().getContentAsString().contains("new realm"));
        //System.out.println(result.getResponse().getContentAsString());

    }


}
