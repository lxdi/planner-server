import controllers.MeansRESTController;
import model.dto.mean.MeansDtoMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import orm_tests.conf.AbstractTestsWithTargetsWithMeans;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansRESTControllerTests extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    MeansDtoMapper meansDtoMapper;

    private MockMvc mockMvc;
    private MeansRESTController meansRESTController;

    @Before
    public void init(){
        super.init();
        meansRESTController = new MeansRESTController(meansDao, meansDtoMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(meansRESTController).build();
    }

    @Test
    public void createTest() throws Exception {
        String content = "{\"id\":0,\"title\":\"new mean\",\"parentid\":2, \"targetsIds\":[1, 4] ,\"children\":[], \"realmid\":1}";
        MvcResult result = mockMvc.perform(put("/mean/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        int newId = 4;
        assertTrue(meansDao.meanById(newId)!=null);
        assertTrue(meansDao.meanById(newId).getTitle().equals("new mean"));
        assertTrue(meansDao.meanById(newId).getTargets().size()==2);
        assertTrue(meansDao.meanById(newId).getTargets().get(0).getTitle().equals(defaultParentTarget) ||
                meansDao.meanById(newId).getTargets().get(0).getTitle().equals(defaultChildChildTarget));
        assertTrue(meansDao.meanById(newId).getTargets().get(1).getTitle().equals(defaultParentTarget) ||
                meansDao.meanById(newId).getTargets().get(1).getTitle().equals(defaultChildChildTarget));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":"+newId));
        assertTrue(result.getResponse().getContentAsString().contains("new mean"));
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void updateTest() throws Exception {
        String content = "{\"id\":1,\"title\":\"Parent mean changed\",\"parentid\":0, \"targetsIds\":[1], \"realmid\":1}";
        MvcResult result = mockMvc.perform(post("/mean/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        assertTrue(meansDao.meanById(1).getTitle().equals("Parent mean changed"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":1"));
        assertTrue(result.getResponse().getContentAsString().contains("Parent mean changed"));
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test(expected = NestedServletException.class)
    public void createMeanWithoutRealm() throws Exception {
        String content = "{\"id\":1,\"title\":\"Parent mean changed\",\"parentid\":0, \"targetsIds\":[1]}";
        MvcResult result = mockMvc.perform(post("/mean/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void createMeanWithNotExistingRealm() {
        String content = "{\"id\":1,\"title\":\"Parent mean changed\",\"parentid\":0, \"targetsIds\":[1], \"realmid\":100500}";
        try {
            MvcResult result = mockMvc.perform(post("/mean/update")
                    .contentType(MediaType.APPLICATION_JSON).content(content))
                    .andExpect(status().isOk()).andReturn();
        } catch (Exception ex){
            if(ex instanceof NestedServletException){
                assertTrue(ex.getMessage().contains("Realm doesn't exist with id = 100500"));
                return;
            }
        }
        throw new AssertionError();
    }
}
