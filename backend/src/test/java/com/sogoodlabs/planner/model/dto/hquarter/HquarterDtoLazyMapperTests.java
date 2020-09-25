package com.sogoodlabs.planner.model.dto.hquarter;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.dao.ISlotPositionDAO;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.dto.HquarterMapper;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.util.StringUtils;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;

import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static com.sogoodlabs.planner.util.DateUtils.toDate;

public class HquarterDtoLazyMapperTests extends AbstractTestsWithTargets {

    @Autowired
    IHQuarterDAO hQuarterDao;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    @Autowired
    private ISlotPositionDAO slotPositionDAO;

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
        slotDAO.save(slot);

        slotPosition = new SlotPosition();
        slotPosition.setSlot(slot);
        slotPosition.setDayOfWeek(DaysOfWeek.thu);
        slotPosition.setPosition(2);
        slotPositionDAO.save(slotPosition);

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
