package orm_tests.conf;

import model.IQuarterDAO;
import model.entities.Quarter;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;


public abstract class ATestsWithTargetsMeansQuartals extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    protected IQuarterDAO quartalDAO;

    protected Quarter emptyQuarter;
    protected Quarter halfEmptyQuarter;
    protected Quarter fullQuarter;

    @Before
    @Override
    public void init(){
        super.init();
        emptyQuarter = new Quarter();
        quartalDAO.saveOrUpdate(emptyQuarter);

        halfEmptyQuarter = new Quarter();
        quartalDAO.saveOrUpdate(halfEmptyQuarter);

        fullQuarter = new Quarter();
        quartalDAO.saveOrUpdate(fullQuarter);

        assertTrue(emptyQuarter.getId()>0);
        assertTrue(halfEmptyQuarter.getId()>0);
        assertTrue(fullQuarter.getId()>0);

    }

}
