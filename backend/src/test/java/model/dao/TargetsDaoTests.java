package model.dao;

import model.entities.Realm;
import model.entities.Target;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import test_configs.AbstractTestsWithTargets;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 10.03.2018.
 */

@Transactional
public class TargetsDaoTests extends AbstractTestsWithTargets {

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void gettingAllTargetsTest(){
        List<Target> targets = targetsDAO.allTargets();
        assertTrue(targets.size()==4);
    }

    @Test
    public void saveOrUpdateTest(){

        sessionFactory.getCurrentSession().clear();

        Target target = new Target("new child", realm);
        target.setId(2);
        targetsDAO.saveOrUpdate(target);

        assertTrue(targetsDAO.targetById(2).getTitle().equals("new child"));
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

    @Test
    public void deletingLastTargetTest(){
        Target target2 = new Target("test Target2", realm);
        targetsDAO.saveOrUpdate(target2);

        Target target1 = new Target("test Target1", realm);
        target1.setNext(target2);
        targetsDAO.saveOrUpdate(target1);

        targetsDAO.deleteTarget(target2.getId());

        assertTrue(targetsDAO.targetById(target2.getId())==null);
        assertTrue(targetsDAO.targetById(target1.getId()).getNext()==null);

    }

    @Test
    public void deletingTargetInTheMiddleTest(){
        Target target3 = new Target("test Target3", realm);
        targetsDAO.saveOrUpdate(target3);

        Target target2 = new Target("test Target2", realm);
        target2.setNext(target3);
        targetsDAO.saveOrUpdate(target2);

        Target target1 = new Target("test Target1", realm);
        target1.setNext(target2);
        targetsDAO.saveOrUpdate(target1);

        targetsDAO.deleteTarget(target2.getId());

        assertTrue(targetsDAO.targetById(target2.getId())==null);
        assertTrue(targetsDAO.targetById(target1.getId()).getNext().getId()==target3.getId());
    }

    @Test
    public void deletingParentTargetTest(){
        Target parentTarget = new Target("test parent Target", realm);
        targetsDAO.saveOrUpdate(parentTarget);

        Target child2 = new Target("test child Target2", realm);
        child2.setParent(parentTarget);
        targetsDAO.saveOrUpdate(child2);

        Target child1 = new Target("test child Target1", realm);
        child1.setParent(parentTarget);
        child1.setNext(child2);
        targetsDAO.saveOrUpdate(child1);

        targetsDAO.deleteTarget(parentTarget.getId());

        assertTrue(targetsDAO.targetById(parentTarget.getId())==null);
        assertTrue(targetsDAO.targetById(child1.getId())==null);
        assertTrue(targetsDAO.targetById(child2.getId())==null);

    }

    @Test
    public void deepDeletingParentTargetTest(){
        Target parentTarget = new Target("test parent Target", realm);
        targetsDAO.saveOrUpdate(parentTarget);

        Target child2 = new Target("test child Target2", realm);
        child2.setParent(parentTarget);
        targetsDAO.saveOrUpdate(child2);

        Target child1 = new Target("test child Target1", realm);
        child1.setParent(parentTarget);
        child1.setNext(child2);
        targetsDAO.saveOrUpdate(child1);

        Target childChild = new Target("test childChild Target", realm);
        childChild.setParent(child1);
        targetsDAO.saveOrUpdate(childChild);

        targetsDAO.deleteTarget(parentTarget.getId());

        assertTrue(targetsDAO.targetById(parentTarget.getId())==null);
        assertTrue(targetsDAO.targetById(child1.getId())==null);
        assertTrue(targetsDAO.targetById(child2.getId())==null);
        assertTrue(targetsDAO.targetById(childChild.getId())==null);

    }

    @Test
    public void isLeafTest(){
        Target rootTarget = createTarget("root target test", null, realm);

        Target target1 = createTarget("target test", null, realm);
        Target target11 = createTarget("child target test 11", target1, realm);
        Target target12 = createTarget("child target test 12", target1, realm);
        Target target121 = createTarget("childchild target test 121", target12, realm);

        assertTrue(targetsDAO.isLeafTarget(rootTarget));
        assertTrue(!targetsDAO.isLeafTarget(target1));
        assertTrue(targetsDAO.isLeafTarget(target11));
        assertTrue(!targetsDAO.isLeafTarget(target12));
        assertTrue(targetsDAO.isLeafTarget(target121));
    }

    private Target createTarget(String title, Target parentTarget, Realm realm){
        Target target = new Target(title, realm);
        target.setParent(parentTarget);
        targetsDAO.saveOrUpdate(target);
        return target;
    }

}
