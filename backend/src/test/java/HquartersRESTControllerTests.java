import controllers.HquartersRESTController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;


import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HquartersRESTControllerTests extends ATestsWithTargetsMeansQuartalsGenerated {

    private MockMvc mockMvc;
    private HquartersRESTController hquartersRESTController;

    @Before
    public void init(){
        super.init();
        hquartersRESTController = new HquartersRESTController(hquarterDAO);
        mockMvc = MockMvcBuilders.standaloneSetup(hquartersRESTController).build();
    }

    @Test
    public void getAllQuartersTest() throws Exception {
        MvcResult result =  mockMvc.perform(get("/quarter/all")).andExpect(status().isOk()).andReturn();
        String resultStr = result.getResponse().getContentAsString();
        assertTrue(resultStr.contains("2018"));
        assertTrue(resultStr.contains("2019"));
    }


}
