package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Slot;
import com.sogoodlabs.planner.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SlotService {

    private static final String NEW_ID_PREFIX = "new_";

    @Autowired
    private ISlotDAO slotDAO;

    public Slot saveOrCreate(Slot slot){

        if (slot.getId() == null || slot.getId().startsWith(NEW_ID_PREFIX)) {

            if(slotDAO.findTotalByRealm(slot.getRealm())>=7){
                throw new RuntimeException("Realm "+slot.getRealm()+" already has at least 7 slots");
            }

            slot.setId(UUID.randomUUID().toString());
        }

        slotDAO.save(slot);
        return slot;
    }


}
