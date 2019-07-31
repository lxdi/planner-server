package controllers.delegates;

import model.dao.IHQuarterDAO;
import model.dao.IWeekDAO;
import model.entities.HQuarter;
import model.entities.Week;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import test_configs.ATestsWithTargetsMeansQuartalsGenerated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Transactional
public class HquartersDelegate_shiftHquarters extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    HquartersDelegate hquartersDelegate;

    @Autowired
    IWeekDAO weekDAO;

    @Test
    public void shiftHquartersSimpleTest(){

        List<HQuarter> beforeShifting = hquarterDAO.getHQuartersInYear(2018);

        Map<Long, Week> oldWeeks = new HashMap<>();
        beforeShifting.forEach(hq -> oldWeeks.put(hq.getId(), hq.getStartWeek()));

        HQuarter selectedHquarter = beforeShifting.get(5);

        hquartersDelegate.shiftHquarters(selectedHquarter.getId());

        List<HQuarter> hQuartersAfterShifting = hquarterDAO.getHQuartersInYear(2018);

        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 2, 0, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 3, 0, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 4, 0, oldWeeks);

        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 5, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 6, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 7, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 8, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 9, 1, oldWeeks);

    }

    private void checkHquarterShifting(List<HQuarter> beforeShift, List<HQuarter> afterShift, int i, int isShifted, Map<Long, Week> oldWeeks){
        assertTrue(afterShift.get(i).getStartWeek().getId()
                == weekDAO.weekByYearAndNumber(2018, oldWeeks.get(beforeShift.get(i).getId()).getNumber()+isShifted).getId());
    }

}
