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
        List<HQuarter> HQuarters = hquarterDAO.getAllHQuartals();

        assertTrue(HQuarters.size()==8);
        int checks = 4;
        for (HQuarter HQuarter : HQuarters) {
            String startWeekDateStr = fromDate(HQuarter.getStartWeek().getStartDay());
            if (startWeekDateStr.contains("2018-01-01")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-03-26")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-06-18")) {
                checks--;
            }
            if (startWeekDateStr.contains("2018-09-10")) {
                checks--;
            }
        }
        assertTrue(checks==0);
    }

}
