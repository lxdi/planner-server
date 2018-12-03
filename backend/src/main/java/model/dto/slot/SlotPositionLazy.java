package model.dto.slot;

import model.entities.DaysOfWeek;

public class SlotPositionLazy {
    long id;
    DaysOfWeek dayOfWeek;
    int position;

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
