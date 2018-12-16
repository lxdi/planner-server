import model.dao.HQuarterDao;
import model.dao.IHQuarterDAO;
import model.dao.ISlotDAO;
import model.dao.IWeekDAO;
import model.dto.hquarter.HquarterDtoFull;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
import model.dto.hquarter.HquarterMapperFull;
import model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.AbstractTestsWithTargets;

import static junit.framework.TestCase.assertTrue;
import static services.DateUtils.fromDate;
import static services.DateUtils.toDate;

public class HquarterMapperTests extends AbstractTestsWithTargets {

    @Autowired
    IHQuarterDAO hQuarterDao;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    @Autowired
    HquarterMapperFull hquarterMapperFull;

    @Autowired
    IWeekDAO weekDAO;

    HQuarter hQuarter;
    SlotPosition slotPosition;
    Slot slot;
    Week startWeek;
    String startDateStr = "2018-05-26";

    @Before
    public void init(){
        super.init();

        startWeek = new Week(toDate(startDateStr), null, 1);
        weekDAO.saveOrUpdate(startWeek);

        hQuarter = new HQuarter();
        hQuarter.setStartWeek(startWeek);
        hQuarterDao.saveOrUpdate(hQuarter);

        slot = new Slot();
        slot.setHquarter(hQuarter);
        slot.setPosition(1);
        slotDAO.saveOrUpdate(slot);

        slotPosition = new SlotPosition();
        slotPosition.setSlot(slot);
        slotPosition.setDaysOfWeek(DaysOfWeek.thu);
        slotPosition.setPosition(2);
        slotDAO.saveOrUpdate(slotPosition);

    }

    @Test
    public void mapToDtoWithDependenciesTest(){
        HquarterDtoFull dtoLazy = hquarterMapperFull.mapToDto(hQuarter);
        assertTrue(dtoLazy.getStartWeek().getId() == startWeek.getId());
        assertTrue(fromDate(dtoLazy.getStartWeek().getStartDay()).equals(startDateStr));
        assertTrue(dtoLazy.getSlots().size()==1);
        assertTrue(dtoLazy.getSlots().get(0).getId() == slot.getId());
        assertTrue(dtoLazy.getSlots().get(0).getSlotPositions().size()==1);
        assertTrue(dtoLazy.getSlots().get(0).getSlotPositions().get(0).getId()==slotPosition.getId());
    }



}
