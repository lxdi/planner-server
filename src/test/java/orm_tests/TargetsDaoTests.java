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
import orm_tests.conf.AbstractTests;
import orm_tests.conf.EmbeddedDBConf;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 10.03.2018.
 */

public class TargetsDaoTests extends AbstractTests{

    @Test
    public void gettingAllTargetsTest(){
        List<Target> targets = targetsDAO.allTargets();
        assertTrue(targets.size()==4);
    }

    @Test
    public void saveOrUpdateTest(){
//        Target parentTarget = new Target("new parent");
//        parentTarget.setId(10);
        Target target = new Target("new child");
        //target.setParent(parentTarget);
        target.setId(2);

        targetsDAO.saveOrUpdate(target);
        assertTrue(targetsDAO.targetById(2).getTitle().equals("new child"));
        //assertTrue(targetsDAO.targetById(1).getTitle().equals("new parent"));
    }

    @Test
    public void getChildrenTest(){
        Target parent = targetsDAO.targetById(1);
        List<Target> childTargets = targetsDAO.getChildren(parent);

        assertTrue(childTargets.size()==2);
        assertTrue(childTargets.get(0).getId()==2);
        assertTrue(childTargets.get(1).getId()==3);
    }

}
