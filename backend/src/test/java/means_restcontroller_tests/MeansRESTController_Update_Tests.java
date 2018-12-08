package means_restcontroller_tests;

import controllers.MeansRESTController;
import model.dto.mean.MeansDtoMapper;
import model.entities.Mean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansRESTController_Update_Tests extends ATestsWithTargetsMeansQuartalsGenerated {

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
    public void updateMeanWithoutExistingId() throws Exception {
        String content = "{\"id\":0,\"title\":\"Parent mean changed\",\"parentid\":0, \"targetsIds\":[1], \"realmid\":1}";
        MvcResult result = mockMvc.perform(post("/mean/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void updateListTest() throws Exception {
        Mean mean = new Mean("parent mean test", realm);
        meansDao.saveOrUpdate(mean);

        Mean child2 = new Mean("child 2 mean test", realm);
        child2.setParent(mean);
        meansDao.saveOrUpdate(child2);

        Mean child1 = new Mean("child 1 mean test", realm);
        child1.setParent(mean);
        child1.setNext(child2);
        meansDao.saveOrUpdate(child1);

        String child1content = "{\"id\":"+child1.getId()+", " +
                "\"title\":\"child 1 mean test\", " +
                "\"realmid\":"+child1.getRealm().getId()+", "+
                "\"parentid\":"+child1.getParent().getId()+", "+
                "\"nextid\":null"+
                "}";

        String child2content = "{\"id\":"+child2.getId()+", " +
                "\"title\":\"child 1 mean test\", " +
                "\"realmid\":"+child2.getRealm().getId()+", "+
                "\"parentid\":"+child2.getParent().getId()+", "+
                "\"nextid\":" +child1.getId()+
                "}";

        String content = "["+child1content+", "+child2content+"]";
        MvcResult result = mockMvc.perform(post("/mean/update/list")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        child1 = meansDao.meanById(child1.getId());
        child2 = meansDao.meanById(child2.getId());

        assertTrue(child1.getNext()==null);
        assertTrue(child2.getNext().getId()==child1.getId());

    }

}
