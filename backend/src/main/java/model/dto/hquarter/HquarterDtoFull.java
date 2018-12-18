package model.dto.hquarter;

import model.dto.slot.SlotDtoLazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HquarterDtoFull extends HquarterDtoLazy{

    List<SlotDtoLazy> slots = new ArrayList<>();
    List<WeekWithTasksDto> weeks  = new ArrayList<>();

    public List<SlotDtoLazy> getSlots() {
        return slots;
    }
    public void setSlots(List<SlotDtoLazy> slots) {
        this.slots = slots;
    }

    public List<WeekWithTasksDto> getWeeks() {
        return weeks;
    }
    public void setWeeks(List<WeekWithTasksDto> weeks) {
        this.weeks = weeks;
    }
}
