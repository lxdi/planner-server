package orm_tests;

import model.entities.Target;
import org.junit.Test;
import orm_tests.conf.AbstractTestsWithTargets;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 10.03.2018.
 */

public class TargetsDaoTests extends AbstractTestsWithTargets {

    @Test
    public void gettingAllTargetsTest(){
        List<Target> targets = targetsDAO.allTargets();
        assertTrue(targets.size()==4);
    }

    @Test
    public void saveOrUpdateTest(){
//        Target parentTarget = new Target("new parent");
//        parentTarget.setId(10);
        Target target = new Target("new child", realm);
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

    @Test
    public void getByTitleTest(){
        assertTrue(targetsDAO.getTargetByTitle(defaultChildTarget).getTitle().equals(defaultChildTarget));
        assertTrue(targetsDAO.getTargetByTitle("100% not existing")==null);
    }
}
