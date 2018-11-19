package orm_tests.conf;

import model.IQuarterDAO;
import model.entities.Quarter;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import services.QuarterGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;


public abstract class ATestsWithTargetsMeansQuartalsGenerated extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    protected IQuarterDAO quarterDAO;

    @Before
    @Override
    public void init(){
        super.init();

        quarterGenerator.generate(new ArrayList<>(Arrays.asList(2018, 2019)));
        List<Quarter> quarters = quarterDAO.getAllQuartals();

        assertTrue(quarters.size()==8);
        int checks = 4;
        for(Quarter quarter : quarters){
            if(quarter.getYear()==2018){
                if(quarter.getStartMonth()==1 && quarter.getStartDay()==1){
                    checks--;
                }
                if(quarter.getStartMonth()==3 && quarter.getStartDay()==26){
                    checks--;
                }
                if(quarter.getStartMonth()==6 && quarter.getStartDay()==18){
                    checks--;
                }
                if(quarter.getStartMonth()==9 && quarter.getStartDay()==10){
                    checks--;
                }
            }
        }
        assertTrue(checks==0);
    }

}
