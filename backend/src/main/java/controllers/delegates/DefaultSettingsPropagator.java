package controllers.delegates;

import model.dao.IHQuarterDAO;
import model.dao.ISlotDAO;
import model.entities.HQuarter;
import model.entities.Slot;
import model.entities.SlotPosition;
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

    public void propagateSettingsFrom(HQuarter defaultHquarter){
        List<Slot> defaultSlots = slotDAO.getSlotsForHquarter(defaultHquarter);
        if(defaultSlots.size()>0) {
            for(HQuarter hQuarter : quarterDAO.getDefaultHquarters()) {
                //List<Slot> slots = slotDAO.getSlotsForHquarter(hQuarter);
                for(Slot defaultSlot :  defaultSlots){
                    Slot slot = slotDAO.getByHquarterAndPosition(hQuarter, defaultSlot.getPosition());
                    if(slot==null){
                        slot = new Slot();
                        slot.setPosition(defaultSlot.getPosition());
                        slot.setHquarter(hQuarter);
                        slotDAO.saveOrUpdate(slot);
                    }
                    Stack<SlotPosition> slotPositionsPool = new Stack<>();
                    for(SlotPosition slotPosition : slotDAO.getSlotPositionsForSlot(slot)){
                        slotPositionsPool.push(slotPosition);
                    }
                    for(SlotPosition defaultSlotPosition : slotDAO.getSlotPositionsForSlot(defaultSlot)){
                        //SlotPosition slotPosition = slotDAO.getSlotPosition(slot, defaultSlotPosition.getDaysOfWeek(), defaultSlotPosition.getPosition());
                        SlotPosition slotPosition = !slotPositionsPool.isEmpty()? slotPositionsPool.pop():null;
                        if(slotPosition==null){
                            slotPosition = new SlotPosition();
                            slotPosition.setSlot(slot);
                            //slotDAO.saveOrUpdate(slotPosition);
                        }
                        if(slotPosition.getDaysOfWeek()!=defaultSlotPosition.getDaysOfWeek() || slotPosition.getPosition()!=defaultSlotPosition.getPosition()){
                            slotPosition.setDaysOfWeek(defaultSlotPosition.getDaysOfWeek());
                            slotPosition.setPosition(defaultSlotPosition.getPosition());
                            slotDAO.saveOrUpdate(slotPosition);
                        }
                    }
                    //TODO remove all SlotPositions left in the slotPositionsPool
                }
            }
        }
    }

}
