package orm_tests.conf;

import model.IQuartalDAO;
import model.entities.Mean;
import model.entities.Quartal;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;


public abstract class ATestsWithTargetsMeansQuartals extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    protected IQuartalDAO quartalDAO;

    protected Quartal emptyQuartal;
    protected Quartal halfEmptyQuartal;
    protected Quartal fullQuartal;

    @Before
    @Override
    public void init(){
        super.init();
        emptyQuartal = new Quartal();
        quartalDAO.saveOrUpdate(emptyQuartal);

        halfEmptyQuartal = new Quartal();
        quartalDAO.saveOrUpdate(halfEmptyQuartal);

        fullQuartal = new Quartal();
        quartalDAO.saveOrUpdate(fullQuartal);

        assertTrue(emptyQuartal.getId()>0);
        assertTrue(halfEmptyQuartal.getId()>0);
        assertTrue(fullQuartal.getId()>0);

    }

}
