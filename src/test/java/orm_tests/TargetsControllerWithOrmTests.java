package orm_tests;

import configuration.HibernateConfigMain;
import controllers.TargetsController;
import model.ITargetsDAO;
import model.TargetsDao;
import model.dto.TargetsDtoMapper;
import model.entities.Target;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.EmbeddedDBConf;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 10.03.2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
        classes = {HibernateConfigMain.class, EmbeddedDBConf.class, TargetsDao.class, TargetsDtoMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TargetsControllerWithOrmTests {

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    TargetsDtoMapper targetsDtoMapper;

    private MockMvc mockMvc;
    private TargetsController targetsController;

    @Before
    public void init(){
        Target parentTarget = new Target("drefault parent");
        Target target = new Target("default child");
        target.setParent(parentTarget);
        targetsDAO.saveOrUpdate(target);

        targetsController = new TargetsController(targetsDAO, targetsDtoMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(targetsController).build();

        assertTrue(parentTarget.getId()==1);
        assertTrue(target.getId()==2);
    }

    @Test
    public void createTest() throws Exception {
        String content = "{\"id\":0,\"title\":\"new target\",\"parentid\":2, \"children\":[]}";
        MvcResult result = mockMvc.perform(put("/target/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        assertTrue(targetsDAO.targetById(3)!=null);
        assertTrue(targetsDAO.targetById(3).getTitle().equals("new target"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":3"));
        assertTrue(result.getResponse().getContentAsString().contains("new target"));
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void deleteTest() throws Exception {
        MvcResult result = mockMvc.perform(delete("/target/delete/2"))
                .andExpect(status().isOk()).andReturn();

        assertTrue(targetsDAO.targetById(1)!=null);
        assertTrue(targetsDAO.targetById(2)==null);
    }

    @Test
    public void updateTest() throws Exception {
        String content = "{\"id\":2,\"title\":\"default child changed\",\"parentid\":1}";
        MvcResult result = mockMvc.perform(post("/target/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        assertTrue(targetsDAO.targetById(3)==null);
        assertTrue(targetsDAO.targetById(2).getTitle().equals("default child changed"));
        assertTrue(result.getResponse().getContentAsString().contains("\"id\":2"));
        assertTrue(result.getResponse().getContentAsString().contains("default child changed"));
        System.out.println(result.getResponse().getContentAsString());
    }




}
