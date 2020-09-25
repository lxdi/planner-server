package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.dao.ISlotPositionDAO;
import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Slot;
import com.sogoodlabs.planner.model.entities.SlotPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Stack;

@Service
@Transactional
public class DefaultSettingsPropagator {

    @Autowired
    IHQuarterDAO quarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    private ISlotPositionDAO slotPositionDAO;

    public void propagateSettingsFrom(HQuarter defaultHquarter){
        List<Slot> defaultSlots = slotDAO.getSlotsForHquarter(defaultHquarter);
        if(defaultSlots.size()>0) {
            for(HQuarter hQuarter : quarterDAO.getWorkingHquarters()) {
                //List<Slot> slots = slotDAO.getSlotsForHquarter(hQuarter);
                for(Slot defaultSlot :  defaultSlots){
                    Slot slot = slotDAO.getByHquarterAndPosition(hQuarter, defaultSlot.getPosition());
                    if(slot==null){
                        slot = new Slot();
                        slot.setPosition(defaultSlot.getPosition());
                        slot.setHquarter(hQuarter);
                        slotDAO.save(slot);
                    }
                    Stack<SlotPosition> slotPositionsPool = new Stack<>();
                    for(SlotPosition slotPosition : slotDAO.getSlotPositionsForSlot(slot)){
                        slotPositionsPool.push(slotPosition);
                    }
                    for(SlotPosition defaultSlotPosition : slotDAO.getSlotPositionsForSlot(defaultSlot)){
                        //SlotPosition slotPosition = slotDAO.getSlotPosition(slot, defaultSlotPosition.getDayOfWeek(), defaultSlotPosition.getPosition());
                        SlotPosition slotPosition = !slotPositionsPool.isEmpty()? slotPositionsPool.pop():null;
                        if(slotPosition==null){
                            slotPosition = new SlotPosition();
                            slotPosition.setSlot(slot);
                            //slotDAO.saveOrUpdate(slotPosition);
                        }
                        if(slotPosition.getDayOfWeek()!=defaultSlotPosition.getDayOfWeek() || slotPosition.getPosition()!=defaultSlotPosition.getPosition()){
                            slotPosition.setDayOfWeek(defaultSlotPosition.getDayOfWeek());
                            slotPosition.setPosition(defaultSlotPosition.getPosition());
                            slotPositionDAO.save(slotPosition);
                        }
                    }
                    //TODO remove all SlotPositions left in the slotPositionsPool
                }
            }
        }
    }

}
