package orm_tests;

import model.entities.HQuarter;
import model.entities.Mean;
import org.junit.Test;
import orm_tests.conf.ATestsWithTargetsMeansQuartals;

import static junit.framework.TestCase.assertTrue;

@Deprecated
public class QuartersTests {//extends ATestsWithTargetsMeansQuartals {

//    @Test
//    public void assignMeanToQuartalTest(){
//        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
//        Mean meanChild = meansDao.meanByTitle(childMeanTitle);
//        Mean meanChild2 = meansDao.meanByTitle(child2MeanTitle);
//
//
//        HQuarter halfEmptyHQuarter = hquartalDAO.getById(this.secondHQuarter.getId());
//        HQuarter fullHQuarter = hquartalDAO.getById(this.thirdHQuarter.getId());
//        assertTrue(hquartalDAO.getMeansOfQuarter(halfEmptyHQuarter).size()==0);
//        assertTrue(hquartalDAO.getMeansOfQuarter(fullHQuarter).size()==0);
//
//        meansDao.assignQuarter(halfEmptyHQuarter, meanChild, 1);
//        assertTrue(hquartalDAO.getMeansOfQuarter(hquartalDAO.getById(this.secondHQuarter.getId())).size()==1);
//        assertTrue(hquartalDAO.getMeansOfQuarter(halfEmptyHQuarter).size()==1);
//
//        meansDao.assignQuarter(fullHQuarter, meanParent, 1);
//        meansDao.assignQuarter(fullHQuarter, meanChild2, 2);
//        assertTrue(hquartalDAO.getMeansOfQuarter(fullHQuarter).size()==2);
//
//        //try to assign second time the same mean
//        meansDao.assignQuarter(halfEmptyHQuarter, meanChild, 1);
//        assertTrue(hquartalDAO.getMeansOfQuarter(hquartalDAO.getById(this.secondHQuarter.getId())).size()==1);
//        assertTrue(hquartalDAO.getMeansOfQuarter(halfEmptyHQuarter).size()==1);
//
//    }

//    @Test
//    public void deleteMeanWithQuarter(){
//        int quartersAtTheBeginning = hquartalDAO.getAllHQuartals().size();
//
//        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
//        meansDao.assignQuarter(thirdHQuarter, meanParent, 1);
//
//        meansDao.deleteMean(meansDao.meanByTitle(parentMeanTitle).getId());
//
//        assertTrue(hquartalDAO.getAllHQuartals().size()==quartersAtTheBeginning);
//    }
//
//    @Test(expected = RuntimeException.class)
//    public void assigningMeanOnOccupiedPlace(){
//        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
//        Mean meanChild = meansDao.meanByTitle(childMeanTitle);
//        Mean meanChild2 = meansDao.meanByTitle(child2MeanTitle);
//
//        meansDao.assignQuarter(hquartalDAO.getById(this.secondHQuarter.getId()), meanParent, 1);
//        meansDao.assignQuarter(hquartalDAO.getById(this.secondHQuarter.getId()), meanChild, 2);
//        meansDao.assignQuarter(hquartalDAO.getById(this.secondHQuarter.getId()), meanChild2, 1);
//    }

}
