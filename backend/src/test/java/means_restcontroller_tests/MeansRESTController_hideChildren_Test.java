package means_restcontroller_tests;

import controllers.MeansRESTController;
import controllers.delegates.MeansDelegate;
import model.dto.mean.MeanDtoLazy;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MeansRESTController_hideChildren_Test {

    @Test
    public void pathVariablesReceivingTest() throws Exception {

        MeansDelegate meansDelegate = new MeansDelegate(){
            @Override
            public MeanDtoLazy hideChildren(long id, boolean val){
                MeanDtoLazy meanDtoLazy = new MeanDtoLazy();
                meanDtoLazy.setHideChildren(val);
                return meanDtoLazy;
            }
        };

        MeansRESTController meansRESTController = new MeansRESTController(meansDelegate);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(meansRESTController).build();

        MvcResult result = mockMvc.perform(post("/mean/1/hideChildren/true"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("\"hideChildren\":true"));

        result = mockMvc.perform(post("/mean/1/hideChildren/false"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("\"hideChildren\":false"));
    }


}
