package orm_tests.conf;

import model.IWeekDAO;
import model.WeeksGenerator;
import model.entities.Week;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 22.04.2018.
 */
public class ATestsWithTargetsMeansWeeks extends AbstractTestsWithTargetsWithMeans {

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

    @Test
    public void weekDAOgetWeeeksOfYearTest(){
        List<Week> weeksIn2018 = weekDAO.getWeeksOfYear(2018);
        assertTrue(weeksIn2018.get(0).getYear()==2018);
        assertTrue(weeksIn2018.get(20).getYear()==2018);
        assertTrue(weeksIn2018.get(35).getYear()==2018);
        assertTrue(weeksIn2018.size()==53);
        assertTrue(weeksIn2018.get(weeksIn2018.size()-1).getYear()==2018);
    }

    @Test
    public void weekDAOgetWeeksMapTest(){
        Map<Integer, List<Week>> weekshmap = weekDAO.getWeeksMap();
        assertTrue(weekshmap.get(2018).size()==53);
        assertTrue(weekshmap.get(2018).get(0).getYear()==2018);
        assertTrue(weekshmap.get(2017).get(0).getYear()==2017);
        assertTrue(weekshmap.get(2019).get(0).getYear()==2019);
    }

}
