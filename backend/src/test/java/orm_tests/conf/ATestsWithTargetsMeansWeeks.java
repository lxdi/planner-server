package orm_tests.conf;

import model.IWeekDAO;
import services.WeeksGenerator;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Alexander on 22.04.2018.
 */
public abstract class ATestsWithTargetsMeansWeeks extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    protected IWeekDAO weekDAO;

    @Autowired
    protected WeeksGenerator weeksGenerator;


    @Before
    @Override
    public void init(){
        super.init();
        weeksGenerator.generate(new ArrayList<>(Arrays.asList(2017, 2018, 2019)));
    }

}
