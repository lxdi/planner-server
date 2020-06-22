package com.sogoodlabs.planner.model.dto.hquarter;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.dto.HquarterMapper;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.services.StringUtils;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;

import java.sql.Date;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static com.sogoodlabs.planner.services.DateUtils.toDate;

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
    IDayDao dayDao;

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


        startWeek = new Week(createDay(toDate(startDateStr)), null, 1);
        weekDAO.saveOrUpdate(startWeek);

        endWeek = new Week(createDay(toDate(startDateStr)), null, 6);
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

    private Day createDay(Date date){
        Day day = new Day(date);
        dayDao.save(day);
        return day;
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
