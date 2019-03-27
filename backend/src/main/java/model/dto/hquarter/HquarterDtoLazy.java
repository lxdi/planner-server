package model.dto.hquarter;

import model.dto.slot.SlotDtoLazy;
import model.entities.Week;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HquarterDtoLazy {

    long id;
    Map<String, Object> startWeek;
    Map<String, Object> endWeek;

    List<SlotDtoLazy> slotsLazy = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Map<String, Object> getStartWeek() {
        return startWeek;
    }
    public void setStartWeek(Map<String, Object> startWeek) {
        this.startWeek = startWeek;
    }

    public Map<String, Object> getEndWeek() {
        return endWeek;
    }
    public void setEndWeek(Map<String, Object> endWeek) {
        this.endWeek = endWeek;
    }

    public List<SlotDtoLazy> getSlotsLazy() {
        return slotsLazy;
    }

    public void setSlotsLazy(List<SlotDtoLazy> slotsLazy) {
        this.slotsLazy = slotsLazy;
    }
}
