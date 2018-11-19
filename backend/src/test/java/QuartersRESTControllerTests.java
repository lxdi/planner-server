import controllers.MeansRESTController;
import controllers.QuartersRESTController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;


import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuartersRESTControllerTests  extends ATestsWithTargetsMeansQuartalsGenerated {

    private MockMvc mockMvc;
    private QuartersRESTController quartersRESTController;

    @Before
    public void init(){
        super.init();
        quartersRESTController = new QuartersRESTController(quarterDAO);
        mockMvc = MockMvcBuilders.standaloneSetup(quartersRESTController).build();
    }

    @Test
    public void getAllWeeksTest() throws Exception {
        MvcResult result =  mockMvc.perform(get("/quarter/all")).andExpect(status().isOk()).andReturn();
        String resultStr = result.getResponse().getContentAsString();
        assertTrue(resultStr.contains("2018"));
        assertTrue(resultStr.contains("2019"));
    }


}
