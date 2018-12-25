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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansRESTController_Creating_Tests extends ATestsWithTargetsMeansQuartalsGenerated {

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
    public void createTest() throws Exception {
        String content = "{\"id\":0,\"title\":\"new mean\",\"parentid\":2, \"targetsIds\":[1, 4] ,\"children\":[], \"realmid\":1}";
        MvcResult result = mockMvc.perform(put("/mean/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        long newId = meansDao.meanByTitle("new mean").getId();
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

    @Test(expected = NestedServletException.class)
    public void createMeanWithoutRealm() throws Exception {
        String content = "{\"title\":\"Parent mean changed\",\"parentid\":0, \"targetsIds\":[1]}";
        MvcResult result = mockMvc.perform(put("/mean/create")
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

    @Test(expected = NestedServletException.class)
    public void createMeanWithIdGtThanZero() throws Exception {
        String content = "{\"id\":1,\"title\":\"new mean\",\"parentid\":2, \"targetsIds\":[1, 4] ,\"children\":[], \"realmid\":1}";
        MvcResult result = mockMvc.perform(put("/mean/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();
    }


    @Test
    public void createMeanOnTheLastPositionTest() throws Exception {
        Mean mean = new Mean("parent mean test", realm);
        meansDao.saveOrUpdate(mean);

        Mean child2 = new Mean("child 2 mean test", realm);
        child2.setParent(mean);
        meansDao.saveOrUpdate(child2);

        Mean child1 = new Mean("child 1 mean test", realm);
        child1.setParent(mean);
        child1.setNext(child2);
        meansDao.saveOrUpdate(child1);

        String content = "{\"id\":0,\"title\":\"new mean to add\"," +
                "\"parentid\":"+mean.getId()+", " +
                "\"children\":[]," +
                "\"realmid\":1}";
        MvcResult result = mockMvc.perform(put("/mean/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        Mean newMean = meansDao.meanByTitle("new mean to add");
        child2 = meansDao.meanById(child2.getId());
        assertTrue(newMean.getParent().getId()==mean.getId());
        assertTrue(newMean.getParent().getId()==mean.getId());
        assertTrue(child2.getNext().getId()==newMean.getId());
    }

    @Test
    public void createMeanOnTheLastPositionOfRootTest() throws Exception {

        Mean root2 = new Mean("root 2 mean test", realm);
        meansDao.saveOrUpdate(root2);

        Mean root1 = new Mean("root 1 mean test", realm);
        root1.setNext(root2);
        meansDao.saveOrUpdate(root1);

        Mean parentMean = meansDao.meanByTitle(parentMeanTitle);
        parentMean.setNext(root1);
        meansDao.saveOrUpdate(parentMean);

        String content = "{\"id\":0,\"title\":\"new root mean to add\"," +
                "\"children\":[]," +
                "\"realmid\":1}";
        MvcResult result = mockMvc.perform(put("/mean/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        Mean newMean = meansDao.meanByTitle("new root mean to add");
        root2 = meansDao.meanById(root2.getId());
        assertTrue(newMean.getParent()==null);
        assertTrue(root2.getNext().getId()==newMean.getId());
    }

}
