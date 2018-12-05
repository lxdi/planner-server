import model.dao.HQuarterDao;
import model.dao.IHQuarterDAO;
import model.dao.ISlotDAO;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
import model.entities.DaysOfWeek;
import model.entities.HQuarter;
import model.entities.Slot;
import model.entities.SlotPosition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.AbstractTestsWithTargets;

import static junit.framework.TestCase.assertTrue;

public class HquarterMapperTests extends AbstractTestsWithTargets {

    @Autowired
    IHQuarterDAO hQuarterDao;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    HQuarter hQuarter;
    SlotPosition slotPosition;
    Slot slot;

    @Before
    public void init(){
        super.init();

        hQuarter = new HQuarter();
        hQuarter.setStartDay(1);
        hQuarter.setStartMonth(2);
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
        HquarterDtoLazy dtoLazy = hquarterMapper.mapToDto(hQuarter);
        assertTrue(dtoLazy.getStartday()==1);
        assertTrue(dtoLazy.getStartmonth()==2);
        assertTrue(dtoLazy.getSlots().size()==1);
        assertTrue(dtoLazy.getSlots().get(0).getId() == slot.getId());
        assertTrue(dtoLazy.getSlots().get(0).getSlotPositions().size()==1);
        assertTrue(dtoLazy.getSlots().get(0).getSlotPositions().get(0).getId()==slotPosition.getId());
    }



}
