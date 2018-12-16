package model.dto.hquarter;

import model.dto.slot.SlotDtoLazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HquarterDtoFull extends HquarterDtoLazy{

    List<SlotDtoLazy> slots = new ArrayList<>();

    public List<SlotDtoLazy> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotDtoLazy> slots) {
        this.slots = slots;
    }
}
