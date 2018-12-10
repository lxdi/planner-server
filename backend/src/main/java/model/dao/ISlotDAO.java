package model.dao;

import model.entities.DaysOfWeek;
import model.entities.HQuarter;
import model.entities.Slot;
import model.entities.SlotPosition;

import java.util.List;

public interface ISlotDAO {

    void saveOrUpdate(Slot slot);
    void saveOrUpdate(SlotPosition slotPosition);
    Slot getById(long id);
    Slot getByHquarterAndPosition(HQuarter hQuarter, int position);
    SlotPosition getSlotPositionById(long id);
    List<Slot> getSlotsForHquarter(HQuarter hquarter);
    List<SlotPosition> getSlotPositionsForSlot(Slot slot);
    SlotPosition getSlotPosition(Slot slot, DaysOfWeek daysOfWeek, int position);

}
