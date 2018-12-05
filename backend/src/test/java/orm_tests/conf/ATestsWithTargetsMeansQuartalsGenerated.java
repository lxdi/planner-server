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
        List<HQuarter> HQuarters = hquarterDAO.getAllHQuartals();

        assertTrue(HQuarters.size()==8);
        int checks = 4;
        for(HQuarter HQuarter : HQuarters){
            if(HQuarter.getYear()==2018){
                if(HQuarter.getStartMonth()==1 && HQuarter.getStartDay()==1){
                    checks--;
                }
                if(HQuarter.getStartMonth()==3 && HQuarter.getStartDay()==26){
                    checks--;
                }
                if(HQuarter.getStartMonth()==6 && HQuarter.getStartDay()==18){
                    checks--;
                }
                if(HQuarter.getStartMonth()==9 && HQuarter.getStartDay()==10){
                    checks--;
                }
            }
        }
        assertTrue(checks==0);
    }

}
