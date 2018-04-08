import controllers.TargetsRESTController;
import model.ITargetsDAO;
import model.dto.target.TargetsDtoMapper;
import model.entities.Target;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 10.03.2018.
 */
public class TargetsRESTControllerTest {

   private MockMvc mockMvc;
   private TargetsRESTController targetsRESTController;

   @Before
   public void init(){
      Target target1  = new Target("target-1");
      target1.setId(1);
      Target target2 = new Target("target-1-2");
      target2.setId(2);
      target2.setParent(target1);
      Target target3 = new Target("target-2");
      target3.setId(3);
      List<Target> listoftargets = new ArrayList<>(Arrays.asList(target1, target2 , target3));

      ITargetsDAO targetsDAOtest = Mockito.mock(ITargetsDAO.class);
      when(targetsDAOtest.allTargets()).thenReturn(listoftargets);

      targetsRESTController = new TargetsRESTController(targetsDAOtest, new TargetsDtoMapper());
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
