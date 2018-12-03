package model.entities;

import javax.persistence.*;

@Entity
public class SlotPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    Slot slot;

    @Enumerated(EnumType.STRING)
    DaysOfWeek daysOfWeek;

    int position;

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

    public DaysOfWeek getDaysOfWeek() {
        return daysOfWeek;
    }
    public void setDaysOfWeek(DaysOfWeek daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
}
