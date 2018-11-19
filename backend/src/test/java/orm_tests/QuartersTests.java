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


        Quarter halfEmptyQuarter = quartalDAO.getById(this.halfEmptyQuarter.getId());
        Quarter fullQuarter = quartalDAO.getById(this.fullQuarter.getId());
        assertTrue(halfEmptyQuarter.getMeans().size()==0);
        assertTrue(fullQuarter.getMeans().size()==0);

        quartalDAO.assignMean(halfEmptyQuarter, meanChild);

        quartalDAO.assignMean(fullQuarter, meanParent);
        quartalDAO.assignMean(fullQuarter, meanChild2);

        assertTrue(quartalDAO.getById(this.halfEmptyQuarter.getId()).getMeans().size()==1);
        assertTrue(halfEmptyQuarter.getMeans().size()==1);
        assertTrue(fullQuarter.getMeans().size()==2);

        quartalDAO.assignMean(halfEmptyQuarter, meanChild);

        assertTrue(quartalDAO.getById(this.halfEmptyQuarter.getId()).getMeans().size()==1);
        assertTrue(halfEmptyQuarter.getMeans().size()==1);

    }
}
