package orm_tests;

import configuration.HibernateConfig;
import configuration.HibernateConfigMain;
import model.ITargetsDAO;
import model.TargetsDao;
import model.entities.Target;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import orm_tests.conf.EmbeddedDBConf;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 10.03.2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
        classes = {HibernateConfigMain.class, EmbeddedDBConf.class, TargetsDao.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TargetsDaoTests {

    @Autowired
    ITargetsDAO targetsDAO;

    @Before
    public void init(){
        Target parentTarget = new Target("drefault parent");
        Target target = new Target("default child");
        target.setParent(parentTarget);
        targetsDAO.saveOrUpdate(target);

        assertTrue(parentTarget.getId()==1);
        assertTrue(target.getId()==2);
    }

    @Test
    public void parentSaveCascadingTest(){}

    @Test
    public void gettingAllTargetsTest(){
        List<Target> targets = targetsDAO.allTargets();
        assertTrue(targets.size()==2);
    }

    @Test
    public void saveOrUpdateTest(){
        Target parentTarget = new Target("new parent");
        parentTarget.setId(1);
        Target target = new Target("new child");
        target.setParent(parentTarget);
        target.setId(2);

        targetsDAO.saveOrUpdate(target);
        assertTrue(targetsDAO.targetById(2).getTitle().equals("new child"));
        assertTrue(targetsDAO.targetById(1).getTitle().equals("new parent"));
    }

}
