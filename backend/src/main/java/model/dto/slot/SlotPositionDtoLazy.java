package model.dto.slot;

import model.entities.DaysOfWeek;

public class SlotPositionDtoLazy {
    Long id;
    DaysOfWeek dayOfWeek;
    int position;
    Long slotid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getSlotid() {
        return slotid;
    }

    public void setSlotid(Long slotid) {
        this.slotid = slotid;
    }
}
