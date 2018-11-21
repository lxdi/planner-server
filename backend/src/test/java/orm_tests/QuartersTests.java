package orm_tests;

import model.entities.Mean;
import model.entities.Quarter;
import org.junit.Test;
import orm_tests.conf.ATestsWithTargetsMeansQuartals;

import static junit.framework.TestCase.assertTrue;

public class QuartersTests extends ATestsWithTargetsMeansQuartals {

    @Test
    public void assignMeanToQuartalTest(){
        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
        Mean meanChild = meansDao.meanByTitle(childMeanTitle);
        Mean meanChild2 = meansDao.meanByTitle(child2MeanTitle);


        Quarter halfEmptyQuarter = quartalDAO.getById(this.secondQuarter.getId());
        Quarter fullQuarter = quartalDAO.getById(this.thirdQuarter.getId());
        assertTrue(quartalDAO.getMeansOfQuarter(halfEmptyQuarter).size()==0);
        assertTrue(quartalDAO.getMeansOfQuarter(fullQuarter).size()==0);

        meansDao.assignQuarter(halfEmptyQuarter, meanChild, 1);
        assertTrue(quartalDAO.getMeansOfQuarter(quartalDAO.getById(this.secondQuarter.getId())).size()==1);
        assertTrue(quartalDAO.getMeansOfQuarter(halfEmptyQuarter).size()==1);

        meansDao.assignQuarter(fullQuarter, meanParent, 1);
        meansDao.assignQuarter(fullQuarter, meanChild2, 2);
        assertTrue(quartalDAO.getMeansOfQuarter(fullQuarter).size()==2);

        //try to assign second time the same mean
        meansDao.assignQuarter(halfEmptyQuarter, meanChild, 1);
        assertTrue(quartalDAO.getMeansOfQuarter(quartalDAO.getById(this.secondQuarter.getId())).size()==1);
        assertTrue(quartalDAO.getMeansOfQuarter(halfEmptyQuarter).size()==1);

    }

    @Test
    public void deleteMeanWithQuarter(){
        int quartersAtTheBeginning = quartalDAO.getAllQuartals().size();

        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
        meansDao.assignQuarter(thirdQuarter, meanParent, 1);

        meansDao.deleteMean(meansDao.meanByTitle(parentMeanTitle).getId());

        assertTrue(quartalDAO.getAllQuartals().size()==quartersAtTheBeginning);
    }

    @Test(expected = RuntimeException.class)
    public void assigningMeanOnOccupiedPlace(){
        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
        Mean meanChild = meansDao.meanByTitle(childMeanTitle);
        Mean meanChild2 = meansDao.meanByTitle(child2MeanTitle);

        meansDao.assignQuarter(quartalDAO.getById(this.secondQuarter.getId()), meanParent, 1);
        meansDao.assignQuarter(quartalDAO.getById(this.secondQuarter.getId()), meanChild, 2);
        meansDao.assignQuarter(quartalDAO.getById(this.secondQuarter.getId()), meanChild2, 1);
    }

}
