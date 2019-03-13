package means_restcontroller_tests;

import controllers.MeansRESTController;
import controllers.delegates.MeansDelegate;
import controllers.delegates.TaskMappersService;
import model.dto.mean.MeansDtoLazyMapper;
import model.entities.Mean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansRESTController_Delete_Tests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    MeansDtoLazyMapper meansDtoLazyMapper;

    @Autowired
    TaskMappersService taskMappersController;

    @Autowired
    MeansDelegate meansDelegate;

    private MockMvc mockMvc;
    private MeansRESTController meansRESTController;

    @Before
    public void init(){
        super.init();
        meansRESTController = new MeansRESTController(meansDelegate);
        mockMvc = MockMvcBuilders.standaloneSetup(meansRESTController).build();
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
