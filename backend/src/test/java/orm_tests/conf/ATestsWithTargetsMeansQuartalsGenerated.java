package orm_tests.conf;

import model.dao.IHQuarterDAO;
import model.entities.HQuarter;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import services.QuarterGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static services.DateUtils.fromDate;


public abstract class ATestsWithTargetsMeansQuartalsGenerated extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    protected IHQuarterDAO hquarterDAO;

    @Before
    @Override
    public void init(){
        super.init();
        quarterGenerator.generate(new ArrayList<>(Arrays.asList(2018, 2019)));
    }

}
