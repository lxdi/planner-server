package orm_tests;

import model.dao.ISlotDAO;
import model.entities.Mean;
import model.entities.Slot;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.AbstractTestsWithTargetsWithMeans;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansDaoTests extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    ISlotDAO slotDAO;

    @Test
    public void deleteTest(){
        meansDao.deleteMean(1);

        assertTrue(meansDao.meanById(1)==null);
        assertTrue(meansDao.meanById(2)==null);
        assertTrue(meansDao.meanById(3)==null);
    }

    @Test
    public void getChildrenTest(){
        List<Mean> children = meansDao.getChildren(meansDao.meanById(1));

        assertTrue(children.size()==2);
        assertTrue(children.get(0).getId()==2 || children.get(0).getId()==3);
        assertTrue(children.get(1).getId()==2 || children.get(1).getId()==3);
    }


    @Test
    public void deleteLinkedTarget(){
        targetsDAO.deleteTarget(targetsDAO.getTargetByTitle(defaultParentTarget).getId());

        assertTrue(meansDao.meanById(1)==null);
        assertTrue(meansDao.meanById(2)==null);
        assertTrue(meansDao.meanById(3)==null);

        assertTrue(targetsDAO.targetById(1)==null);
        assertTrue(targetsDAO.targetById(2)==null);
        assertTrue(targetsDAO.targetById(3)==null);
        assertTrue(targetsDAO.targetById(4)==null);
    }

    @Test
    public void deleteLinkedTarget2(){
        targetsDAO.deleteTarget(targetsDAO.getTargetByTitle(defaultChildTarget).getId());

        assertTrue(meansDao.meanById(1)!=null);
        assertTrue(meansDao.meanById(2)!=null);
        assertTrue(meansDao.meanById(3)!=null);

        assertTrue(targetsDAO.targetById(1)!=null);
        assertTrue(targetsDAO.targetById(2)==null);
        assertTrue(targetsDAO.targetById(3)!=null);
        assertTrue(targetsDAO.targetById(4)!=null);
    }

    @Test
    public void meanByTitleTest(){
        Mean mean = meansDao.meanByTitle(parentMeanTitle);
        assertTrue(mean!=null);
        assertTrue(mean.getTitle().equals(parentMeanTitle));
    }

    @Test
    public void getAllTest(){
        List<Mean> allMeans = meansDao.getAllMeans();

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
        meansDao.saveOrUpdate(mean);

        Slot slot = new Slot();
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);

        Mean mean2 = new Mean("Mean test 2", realm);
        meansDao.saveOrUpdate(mean2);

        Mean mean3 = new Mean("Mean test 3", realm);
        meansDao.saveOrUpdate(mean3);

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
}
