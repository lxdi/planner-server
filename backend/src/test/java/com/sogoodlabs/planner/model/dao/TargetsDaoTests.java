package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import com.sogoodlabs.planner.services.GracefulDeleteService;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Alexander on 10.03.2018.
 */

@Transactional
public class TargetsDaoTests extends AbstractTestsWithTargets {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private GracefulDeleteService gracefulDeleteService;
    
    @Test
    public void gettingAllTargetsTest(){
        List<Target> targets = targetsDAO.findAll();
        assertTrue(targets.size()==4);
    }

    @Test
    public void saveOrUpdateTest(){

        entityManager.unwrap(Session.class).clear();

        Target target = new Target("new child", realm);
        target.setId(2);
        targetsDAO.save(target);

        assertTrue(targetsDAO.getOne(2l).getTitle().equals("new child"));
    }

    @Test
    public void getChildrenTest(){
        Target parent = targetsDAO.getOne(1l);
        List<Target> childTargets = targetsDAO.getChildren(parent);

        assertTrue(childTargets.size()==2);
        assertTrue(childTargets.get(0).getId()==2);
        assertTrue(childTargets.get(1).getId()==3);
    }

    @Test
    public void getLastChildTest(){
        Target parent = targetsDAO.getOne(1l);
        Target childTarget = targetsDAO.getLastOfChildren(parent, realm);

        assertNotNull(childTarget);
    }

    @Test
    public void getByTitleTest(){
        assertTrue(targetsDAO.getTargetByTitle(defaultChildTarget).getTitle().equals(defaultChildTarget));
        assertTrue(targetsDAO.getTargetByTitle("100% not existing")==null);
    }

    @Test
    public void deletingLastTargetTest(){
        Target target2 = new Target("test Target2", realm);
        targetsDAO.save(target2);

        Target target1 = new Target("test Target1", realm);
        target1.setNext(target2);
        targetsDAO.save(target1);

        gracefulDeleteService.deleteTarget(target2.getId());



        assertFalse(isExists(target2.getId(), Target.class));
        assertNull(targetsDAO.getOne(target1.getId()).getNext());

    }

    @Test
    public void deletingTargetInTheMiddleTest(){
        Target target3 = new Target("test Target3", realm);
        targetsDAO.save(target3);

        Target target2 = new Target("test Target2", realm);
        target2.setNext(target3);
        targetsDAO.save(target2);

        Target target1 = new Target("test Target1", realm);
        target1.setNext(target2);
        targetsDAO.save(target1);

        gracefulDeleteService.deleteTarget(target2.getId());

        assertFalse(isExists(target2.getId(), Target.class));
        assertEquals(targetsDAO.getOne(target1.getId()).getNext().getId(), target3.getId());
    }

    @Test
    public void deletingParentTargetTest(){
        Target parentTarget = new Target("test parent Target", realm);
        targetsDAO.save(parentTarget);

        Target child2 = new Target("test child Target2", realm);
        child2.setParent(parentTarget);
        targetsDAO.save(child2);

        Target child1 = new Target("test child Target1", realm);
        child1.setParent(parentTarget);
        child1.setNext(child2);
        targetsDAO.save(child1);

        gracefulDeleteService.deleteTarget(parentTarget.getId());

        assertFalse(isExists(parentTarget.getId(), Target.class));
        assertFalse(isExists(child1.getId(), Target.class));
        assertFalse(isExists(child2.getId(), Target.class));

    }

    @Test
    public void deepDeletingParentTargetTest(){
        Target parentTarget = new Target("test parent Target", realm);
        targetsDAO.save(parentTarget);

        Target child2 = new Target("test child Target2", realm);
        child2.setParent(parentTarget);
        targetsDAO.save(child2);

        Target child1 = new Target("test child Target1", realm);
        child1.setParent(parentTarget);
        child1.setNext(child2);
        targetsDAO.save(child1);

        Target childChild = new Target("test childChild Target", realm);
        childChild.setParent(child1);
        targetsDAO.save(childChild);

        gracefulDeleteService.deleteTarget(parentTarget.getId());

        assertFalse(isExists(parentTarget.getId(), Target.class));
        assertFalse(isExists(child1.getId(), Target.class));
        assertFalse(isExists(child2.getId(), Target.class));
        assertFalse(isExists(childChild.getId(), Target.class));

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
        targetsDAO.save(target);
        return target;
    }

}
