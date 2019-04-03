package model.entities;

import javax.persistence.*;

@Entity
public class SlotPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Slot slot;

    @Enumerated(EnumType.STRING)
    DaysOfWeek dayOfWeek;

    int position;

    public SlotPosition(){}

    public SlotPosition(Slot slot, DaysOfWeek dayOfWeek, int position){
        this.slot = slot;
        this.dayOfWeek = dayOfWeek;
        this.position = position;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Slot getSlot() {
        return slot;
    }
    public void setSlot(Slot slot) {
        this.slot = slot;
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
