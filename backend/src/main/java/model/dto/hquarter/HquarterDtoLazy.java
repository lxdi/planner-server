package model.dto.hquarter;

import model.dto.slot.SlotDtoLazy;

import java.util.ArrayList;
import java.util.List;

public class HquarterDtoLazy {

    long id;
    int year;
    int startmonth;
    int startday;

    List<SlotDtoLazy> slots = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public int getStartmonth() {
        return startmonth;
    }
    public void setStartmonth(int startmonth) {
        this.startmonth = startmonth;
    }

    public int getStartday() {
        return startday;
    }
    public void setStartday(int startday) {
        this.startday = startday;
    }

    public List<SlotDtoLazy> getSlots() {
        return slots;
    }
    public void setSlots(List<SlotDtoLazy> slots) {
        this.slots = slots;
    }
}
