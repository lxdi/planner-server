package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.model.entities.Slot;
import com.sogoodlabs.planner.model.entities.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargetsWithMeans;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Alexander on 08.04.2018.
 */

@Transactional
public class MeansDaoTests extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    ISlotDAO slotDAO;

    @Test
    public void getChildrenTest(){
        List<Mean> children = meansDao.getChildren(meansDao.getOne(1l));

        assertTrue(children.size()==2);
        assertTrue(children.get(0).getId()==2 || children.get(0).getId()==3);
        assertTrue(children.get(1).getId()==2 || children.get(1).getId()==3);
    }


    @Test
    public void deleteLinkedTarget(){
        targetsDAO.deleteTarget(targetsDAO.getTargetByTitle(defaultParentTarget).getId());

        assertTrue(meansDao.getOne(1l)!=null);
        assertTrue(meansDao.getOne(2l)!=null);
        assertTrue(meansDao.getOne(3l)!=null);

        assertTrue(targetsDAO.targetById(1)==null);
        assertTrue(targetsDAO.targetById(2)==null);
        assertTrue(targetsDAO.targetById(3)==null);
        assertTrue(targetsDAO.targetById(4)==null);
    }

    @Test
    public void deleteLinkedTarget2(){
        targetsDAO.deleteTarget(targetsDAO.getTargetByTitle(defaultChildTarget).getId());

        assertTrue(meansDao.getOne(1l)!=null);
        assertTrue(meansDao.getOne(2l)!=null);
        assertTrue(meansDao.getOne(3l)!=null);

        assertNotNull(targetsDAO.targetById(parentTarget.getId()));
        assertNull(targetsDAO.targetById(target.getId()));
        assertNotNull(targetsDAO.targetById(target2.getId()));
        assertNotNull(targetsDAO.targetById(target3.getId()));
    }

    @Test
    public void meanByTitleTest(){
        Mean mean = meansDao.meanByTitle(parentMeanTitle);
        assertTrue(mean!=null);
        assertTrue(mean.getTitle().equals(parentMeanTitle));
    }

    @Test
    public void getAllTest(){
        List<Mean> allMeans = meansDao.findAll();

        boolean find = false;
        for(Mean mean : allMeans){
            if(mean.getTitle().equals(parentMeanTitle)){
                find = true;
                assertTrue(mean.getTargets().size()==1);
            }
        }
        assertTrue(find);
    }

    @Test
    public void assignsMeansCountTest(){
        Mean mean = new Mean("Mean test 1", realm);
        meansDao.save(mean);

        Slot slot = new Slot();
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);

        Mean mean2 = new Mean("Mean test 2", realm);
        meansDao.save(mean2);

        Mean mean3 = new Mean("Mean test 3", realm);
        meansDao.save(mean3);

        Slot slot2 = new Slot();
        slot2.setMean(mean3);
        slotDAO.saveOrUpdate(slot2);

        Slot slot3 = new Slot();
        slot3.setMean(mean3);
        slotDAO.saveOrUpdate(slot3);

        assertTrue(meansDao.assignsMeansCount(mean)==1);
        assertTrue(meansDao.assignsMeansCount(mean3)==2);
        assertTrue(meansDao.assignsMeansCount(mean2)==0);

    }

    @Test
    public void updateMeanWithRemovingTargetAssignsTest(){
        Target target1 = new Target("target test 1", realm);
        targetsDAO.saveOrUpdate(target1);

        Target target2 = new Target("target test 2", realm);
        targetsDAO.saveOrUpdate(target2);

        Mean mean = new Mean("Mean test 1", realm);
        mean.getTargets().add(target1);
        mean.getTargets().add(target2);
        meansDao.save(mean);

        mean = meansDao.getOne(mean.getId());
        assertTrue(mean.getTargets().size()==2);

        mean.getTargets().remove(0);
        mean.getTargets().remove(0);
        meansDao.save(mean);

        mean = meansDao.getOne(mean.getId());
        assertTrue(mean.getTargets().size()==0);

    }

    @Test
    public void numberOfMeansAssignedToTarget(){
        Target target = new Target("target test 1", realm);
        targetsDAO.saveOrUpdate(target);

        Mean mean = new Mean("Mean test 1", realm);
        mean.getTargets().add(target);
        meansDao.save(mean);

        Mean mean2 = new Mean("Mean test 1", realm);
        mean2.getTargets().add(target);
        meansDao.save(mean2);

        assertTrue(meansDao.meansAssignedToTarget(target).size()==2);

    }

}
