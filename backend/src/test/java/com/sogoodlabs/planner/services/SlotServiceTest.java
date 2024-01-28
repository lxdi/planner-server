package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.model.entities.DaysOfWeek;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Slot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class SlotServiceTest extends SpringTestConfig {

    @Autowired private SlotService slotService;

    @Test
    void saveTest_nullRealm() {
        var slot = createSlot(null, DaysOfWeek.mon, 2);
        slotService.save(slot);
    }

    private Slot createSlot(Realm realm, DaysOfWeek daysOfWeek, int hours) {
        var slot = new Slot();
        slot.setId(UUID.randomUUID().toString());
        slot.setHours(hours);
        slot.setRealm(realm);
        slot.setDayOfWeek(daysOfWeek);
        return slot;
    }

}
