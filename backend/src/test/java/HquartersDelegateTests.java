import controllers.delegates.HquartersDelegate;
import model.dao.IHQuarterDAO;
import model.dto.hquarter.HquarterDtoLazy;
import model.entities.HQuarter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.AbstractTestsWithTargets;
import services.DateUtils;
import services.QuarterGenerator;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class HquartersDelegateTests extends AbstractTestsWithTargets {

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO hQuarterDAO;

    @Autowired
    HquartersDelegate hquartersDelegate;

    @Before
    public void init(){
        super.init();
    }

    @Test
    public void getPrevTest(){
        quarterGenerator.generateYear(2018);
        List<HQuarter> hQuarters2018 = hQuarterDAO.getHQuartersInYear(2018);
        HQuarter firstHquarter = hQuarters2018.get(0);
        assertTrue(DateUtils.fromDate(firstHquarter.getStartWeek().getStartDay()).equals("2018-01-01"));
        assertTrue(hQuarterDAO.getHQuartersInYear(2017).size()==0);

        List<HquarterDtoLazy> hQuartersLazy2017 = hquartersDelegate.getPrev(firstHquarter.getId(), 4);

        assertTrue(hQuartersLazy2017.size()==4);
        assertTrue(DateUtils.fromDate(hQuartersLazy2017.get(0).getStartWeek().getStartDay()).equals("2017-08-14"));

    }

    @Test
    public void getNextTest(){
        quarterGenerator.generateYear(2018);
        List<HQuarter> hQuarters2018 = hQuarterDAO.getHQuartersInYear(2018);
        HQuarter lastHquarter = hQuarters2018.get(11);
        assertTrue(DateUtils.fromDate(lastHquarter.getStartWeek().getStartDay()).equals("2018-11-05"));
        assertTrue(hQuarterDAO.getHQuartersInYear(2019).size()==0);

        List<HquarterDtoLazy> hQuartersLazy2019 = hquartersDelegate.getNext(lastHquarter.getId(), 4);

        assertTrue(hQuartersLazy2019.size()==4);
        assertTrue(DateUtils.fromDate(hQuartersLazy2019.get(0).getStartWeek().getStartDay()).equals("2019-01-07"));

    }

    @Test
    public void getCurrentHquartersWithoutSlotsTest(){
        hquartersDelegate.getCurrentHquarters();
    }

}
