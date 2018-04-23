package orm_tests;

import controllers.TargetsRESTController;
import controllers.WeeksRESTController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.ATestsWithTargetsMeansWeeks;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 23.04.2018.
 */
public class WeeksRESTControllerTests extends ATestsWithTargetsMeansWeeks {

    MockMvc mockMvc;
    WeeksRESTController weeksRESTController;

    @Before
    public void init(){
        super.init();
        weeksRESTController = new WeeksRESTController(weekDAO);
        mockMvc = MockMvcBuilders.standaloneSetup(weeksRESTController).build();
    }

    @Test
    public void getAllWeeksTest() throws Exception {
        MvcResult result =  mockMvc.perform(get("/weeks/all")).andExpect(status().isOk()).andReturn();
        String resultStr = result.getResponse().getContentAsString();
        assertTrue(resultStr.contains("2017"));
        assertTrue(resultStr.contains("2018"));
        assertTrue(resultStr.contains("2019"));
    }


}
