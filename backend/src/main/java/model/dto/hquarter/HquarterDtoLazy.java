package model.dto.hquarter;

import model.dto.slot.SlotDtoLazy;
import model.entities.Week;

import java.util.ArrayList;
import java.util.List;

public class HquarterDtoLazy {

    long id;

    Week startWeek;

    Week endWeek;

    List<SlotDtoLazy> slots = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public List<SlotDtoLazy> getSlots() {
        return slots;
    }
    public void setSlots(List<SlotDtoLazy> slots) {
        this.slots = slots;
    }

    public Week getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Week startWeek) {
        this.startWeek = startWeek;
    }

    public Week getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(Week endWeek) {
        this.endWeek = endWeek;
    }
}
