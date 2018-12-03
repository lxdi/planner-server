package model.dto.slot;

import model.entities.DaysOfWeek;

public class SlotPositionDtoLazy {
    long id;
    DaysOfWeek dayOfWeek;
    int position;
    long slotid;

    public long getSlotid() {
        return slotid;
    }
    public void setSlotid(long slotid) {
        this.slotid = slotid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DaysOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DaysOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
