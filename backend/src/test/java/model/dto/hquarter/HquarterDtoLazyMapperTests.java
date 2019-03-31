package model.dto.hquarter;

import model.dao.IHQuarterDAO;
import model.dao.ISlotDAO;
import model.dao.IWeekDAO;
import model.dto.HquarterMapper;
import model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import services.StringUtils;
import test_configs.AbstractTestsWithTargets;

import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static services.DateUtils.toDate;

public class HquarterDtoLazyMapperTests extends AbstractTestsWithTargets {

    @Autowired
    IHQuarterDAO hQuarterDao;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    HQuarter hQuarter;
    SlotPosition slotPosition;
    Slot slot;
    Week startWeek;
    Week endWeek;
    String startDateStr = "2018-05-26";
    String endDateStr = "2018-07-28";

    @Before
    public void init(){
        super.init();

        startWeek = new Week(toDate(startDateStr), null, 1);
        weekDAO.saveOrUpdate(startWeek);

        endWeek = new Week(toDate(startDateStr), null, 6);
        weekDAO.saveOrUpdate(endWeek);

        hQuarter = new HQuarter();
        hQuarter.setStartWeek(startWeek);
        hQuarter.setEndWeek(endWeek);
        hQuarterDao.saveOrUpdate(hQuarter);

        slot = new Slot();
        slot.setHquarter(hQuarter);
        slot.setPosition(1);
        slotDAO.saveOrUpdate(slot);

        slotPosition = new SlotPosition();
        slotPosition.setSlot(slot);
        slotPosition.setDayOfWeek(DaysOfWeek.thu);
        slotPosition.setPosition(2);
        slotDAO.saveOrUpdate(slotPosition);

    }

    @Test
    public void mapToDtoWithDependenciesTest(){
        Map<String, Object> dtoLazy = hquarterMapper.mapToDtoLazy(hQuarter);
        assertTrue((long)StringUtils.getValue(dtoLazy, "get('startWeek').get('id')")== startWeek.getId());
        assertTrue(StringUtils.getValue(dtoLazy, "get('startWeek').get('startDay')").equals(startDateStr));
        assertTrue((int)StringUtils.getValue(dtoLazy, "get('slotsLazy').size()")==1);
        assertTrue((long)StringUtils.getValue(dtoLazy, "get('slotsLazy').get(0).get('id')")==slot.getId());
    }



}
