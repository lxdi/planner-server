package orm_tests.conf;

import model.IQuarterDAO;
import model.entities.Quarter;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;


public abstract class ATestsWithTargetsMeansQuartals extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    protected IQuarterDAO quartalDAO;

    protected Quarter firstQuarter;
    protected Quarter secondQuarter;
    protected Quarter thirdQuarter;

    @Before
    @Override
    public void init(){
        super.init();
        firstQuarter = new Quarter();
        quartalDAO.saveOrUpdate(firstQuarter);

        secondQuarter = new Quarter();
        quartalDAO.saveOrUpdate(secondQuarter);

        thirdQuarter = new Quarter();
        quartalDAO.saveOrUpdate(thirdQuarter);

        assertTrue(firstQuarter.getId()>0);
        assertTrue(secondQuarter.getId()>0);
        assertTrue(thirdQuarter.getId()>0);

    }

}
