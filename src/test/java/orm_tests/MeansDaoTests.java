package orm_tests;

import model.entities.Mean;
import org.junit.Test;
import orm_tests.conf.AbstractTestsWithTargetsWithMeans;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeansDaoTests extends AbstractTestsWithTargetsWithMeans {

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
}
