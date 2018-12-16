package model.dto.hquarter;

import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotLazyTemp;
import model.entities.SlotPosition;
import model.entities.Week;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HquarterDtoLazy {

    long id;

    Week startWeek;

    Week endWeek;

    List<SlotLazyTemp> slotsLazy = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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

    public List<SlotLazyTemp> getSlotsLazy() {
        return slotsLazy;
    }

    public void setSlotsLazy(List<SlotLazyTemp> slotsLazy) {
        this.slotsLazy = slotsLazy;
    }
}
