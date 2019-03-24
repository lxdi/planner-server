package test_configs;

import model.dao.IHQuarterDAO;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import services.QuarterGenerator;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;


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
