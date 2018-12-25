package means_restcontroller_tests;

import controllers.MeansRESTController;
import controllers.delegates.TaskMappersController;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansRESTController_Update_Tests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    MeansDtoMapper meansDtoMapper;

    @Autowired
    TaskMappersController taskMappersController;

    private MockMvc mockMvc;
    private MeansRESTController meansRESTController;

    @Before
    public void init(){
        super.init();
        meansRESTController = new MeansRESTController(meansDao, meansDtoMapper, realmDAO, taskMappersController);
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

    @Test
    public void updateListAndGetTest() throws Exception {

        Mean mean3 = new Mean("mean3", realm);
        meansDao.saveOrUpdate(mean3);

        Mean mean2 = new Mean("mean2", realm);
        mean2.setNext(mean3);
        meansDao.saveOrUpdate(mean2);

        Mean mean1 = new Mean("mean1", realm);
        mean1.setNext(mean2);
        meansDao.saveOrUpdate(mean1);

        String mean1content = "{\"id\":"+mean1.getId()+", " +
                "\"title\":\"mean1\", " +
                "\"realmid\":"+mean1.getRealm().getId()+", "+
                "\"nextid\":" +mean3.getId()+
                "}";

        String mean2content = "{\"id\":"+mean2.getId()+", " +
                "\"title\":\"mean2\", " +
                "\"realmid\":"+mean2.getRealm().getId()+", "+
                "\"nextid\":null" +
                "}";

        String mean3content = "{\"id\":"+mean3.getId()+", " +
                "\"title\":\"mean3\", " +
                "\"realmid\":"+mean3.getRealm().getId()+", "+
                "\"nextid\":" +mean2.getId()+
                "}";

        String content = "["+mean1content+", "+mean3content+", "+mean2content+"]";
        MvcResult result = mockMvc.perform(post("/mean/update/list")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        MvcResult getresult = mockMvc.perform(get("/mean/all/lazy"))
                .andExpect(status().isOk()).andReturn();

        mean2 = meansDao.meanById(mean2.getId());
        assertTrue(mean2.getNext()==null);

    }

}
