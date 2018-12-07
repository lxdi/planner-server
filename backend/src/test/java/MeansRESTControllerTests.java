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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansRESTControllerTests extends ATestsWithTargetsMeansQuartalsGenerated {

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
    public void deletingTest() throws Exception {
        Mean lastMean = new Mean("last mean", realm);
        meansDao.saveOrUpdate(lastMean);

        Mean middleMean = new Mean("middle mean", realm);
        middleMean.setNext(lastMean);
        meansDao.saveOrUpdate(middleMean);

        Mean firstMean = new Mean("first mean", realm);
        firstMean.setNext(middleMean);
        meansDao.saveOrUpdate(firstMean);

        MvcResult result = mockMvc.perform(delete("/mean/delete/"+middleMean.getId()))
                .andExpect(status().isOk()).andReturn();

        firstMean = meansDao.meanById(firstMean.getId());
        assertTrue(firstMean.getNext().getId()==lastMean.getId());

        middleMean = meansDao.meanById(middleMean.getId());
        assertTrue(middleMean==null);

    }

    @Test
    public void deletingWithChildrenTest() throws Exception {
        Mean parentMean = new Mean("test parent mean", realm);
        meansDao.saveOrUpdate(parentMean);

        Mean child1 = new Mean("child 1 mean", realm);
        child1.setParent(parentMean);
        meansDao.saveOrUpdate(child1);

        Mean child2 = new Mean("child 2 mean", realm);
        child2.setNext(child1);
        child2.setParent(parentMean);
        meansDao.saveOrUpdate(child2);

        Mean child3 = new Mean("child 3 mean", realm);
        child3.setNext(child2);
        child3.setParent(parentMean);
        meansDao.saveOrUpdate(child3);

        Mean childChild = new Mean("childChild 1 mean", realm);
        childChild.setParent(child2);
        meansDao.saveOrUpdate(childChild);

        MvcResult result = mockMvc.perform(delete("/mean/delete/"+parentMean.getId()))
                .andExpect(status().isOk()).andReturn();

        assertTrue(meansDao.meanById(parentMean.getId())==null);
        assertTrue(meansDao.meanById(child1.getId())==null);
        assertTrue(meansDao.meanById(child2.getId())==null);
        assertTrue(meansDao.meanById(child3.getId())==null);
        assertTrue(meansDao.meanById(childChild.getId())==null);

    }

//    @Test
//    public void replaceToOtherMeanTest() throws Exception {
//        Mean newParent = new Mean("new parent test", realm);
//        meansDao.saveOrUpdate(newParent);
//        assertTrue(newParent.getId()>0);
//
//        Mean mean = meansDao.meanByTitle(childMeanTitle);
//        assertTrue(mean!=null && mean.getId()>0);
//
//        MvcResult result = mockMvc.perform(post("/mean/replace/"+mean.getId()+"/"+newParent.getId()))
//                .andExpect(status().isOk()).andReturn();
//
//        mean = meansDao.meanById(mean.getId());
//        assertTrue(mean.getParent().getId() == newParent.getId());
//
//    }

//    @Test
//    public void replaceToRootTest() throws Exception {
//        Mean mean = meansDao.meanByTitle(childMeanTitle);
//        assertTrue(mean.getParent()!=null);
//        assertTrue(mean!=null && mean.getId()>0);
//
//        MvcResult result = mockMvc.perform(post("/mean/replace/"+mean.getId()+"/0"))
//                .andExpect(status().isOk()).andReturn();
//
//        mean = meansDao.meanById(mean.getId());
//        assertTrue(mean.getParent()==null);
//
//    }

//    @Test(expected = NestedServletException.class)
//    public void replaceNotExistingTest() throws Exception {
//        MvcResult result = mockMvc.perform(post("/mean/replace/100500/0"))
//                .andExpect(status().isOk()).andReturn();
//    }

}
