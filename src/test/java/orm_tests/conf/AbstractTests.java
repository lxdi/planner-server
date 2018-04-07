package orm_tests.conf;

import configuration.HibernateConfigMain;
import controllers.TargetsController;
import model.ITargetsDAO;
import model.TargetsDao;
import model.dto.TargetsDtoMapper;
import model.entities.Target;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 07.04.2018.
 */

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
        classes = {HibernateConfigMain.class, EmbeddedDBConf.class, TargetsDao.class, TargetsDtoMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AbstractTests {

    @Autowired
    protected ITargetsDAO targetsDAO;

    @Before
    public void init(){
        Target parentTarget = new Target("drefault parent");
        targetsDAO.saveOrUpdate(parentTarget);
        Target target = new Target("default child");
        Target target2 = new Target("default child2");
        Target target3 = new Target("default child child");
        target.setParent(parentTarget);
        target2.setParent(parentTarget);
        target3.setParent(target2);
        targetsDAO.saveOrUpdate(target);
        targetsDAO.saveOrUpdate(target2);
        targetsDAO.saveOrUpdate(target3);

        assertTrue(parentTarget.getId()==1);
        assertTrue(target.getId()==2);
        assertTrue(target2.getId()==3);
        assertTrue(target3.getId()==4);
    }

}
