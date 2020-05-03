package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;

import java.util.List;

public interface ISlotDAO {

    void saveOrUpdate(Slot slot);
    void saveOrUpdate(SlotPosition slotPosition);
    Slot getById(long id);
    Slot getByHquarterAndPosition(HQuarter hQuarter, int position);
    SlotPosition getSlotPositionById(long id);
    List<Slot> getSlotsForHquarter(HQuarter hquarter);
    List<Slot> getSlotsForHquarters(List<HQuarter> hquarters);
    List<Slot> slotsAfter(Slot slot);
    List<SlotPosition> getSlotPositionsForSlot(Slot slot);
    SlotPosition getSlotPosition(Slot slot, DaysOfWeek daysOfWeek, int position);
    List<Slot> slotsWithMean(Mean mean);
    List<Slot> slotsWithLayers(List<Layer> layers);

}
