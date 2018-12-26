package model.dto.hquarter;

import model.dto.slot.SlotDto;

import java.util.ArrayList;
import java.util.List;

public class HquarterDtoFull extends HquarterDtoLazy{

    List<SlotDto> slots = new ArrayList<>();
    List<WeekWithTasksDto> weeks  = new ArrayList<>();

    public List<SlotDto> getSlots() {
        return slots;
    }
    public void setSlots(List<SlotDto> slots) {
        this.slots = slots;
    }

    public List<WeekWithTasksDto> getWeeks() {
        return weeks;
    }
    public void setWeeks(List<WeekWithTasksDto> weeks) {
        this.weeks = weeks;
    }
}
