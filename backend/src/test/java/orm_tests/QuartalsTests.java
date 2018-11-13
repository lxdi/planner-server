package orm_tests;

import model.entities.Mean;
import model.entities.Quartal;
import org.junit.Test;
import orm_tests.conf.ATestsWithTargetsMeansQuartals;

import static junit.framework.TestCase.assertTrue;

public class QuartalsTests extends ATestsWithTargetsMeansQuartals {

    @Test
    public void assignMeanToQuartalTest(){
        Mean meanParent = meansDao.meanByTitle(parentMeanTitle);
        Mean meanChild = meansDao.meanByTitle(childMeanTitle);
        Mean meanChild2 = meansDao.meanByTitle(child2MeanTitle);


        Quartal halfEmptyQuartal = quartalDAO.getById(this.halfEmptyQuartal.getId());
        Quartal fullQuartal = quartalDAO.getById(this.fullQuartal.getId());
        assertTrue(halfEmptyQuartal.getMeans().size()==0);
        assertTrue(fullQuartal.getMeans().size()==0);

        quartalDAO.assignMean(halfEmptyQuartal, meanChild);

        quartalDAO.assignMean(fullQuartal, meanParent);
        quartalDAO.assignMean(fullQuartal, meanChild2);

        assertTrue(quartalDAO.getById(this.halfEmptyQuartal.getId()).getMeans().size()==1);
        assertTrue(halfEmptyQuartal.getMeans().size()==1);
        assertTrue(fullQuartal.getMeans().size()==2);

        quartalDAO.assignMean(halfEmptyQuartal, meanChild);

        assertTrue(quartalDAO.getById(this.halfEmptyQuartal.getId()).getMeans().size()==1);
        assertTrue(halfEmptyQuartal.getMeans().size()==1);

    }
}
