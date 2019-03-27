package model.entities;

import javax.persistence.*;

@Entity
public class SlotPosition implements Comparable<SlotPosition> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Slot slot;

    @Enumerated(EnumType.STRING)
    DaysOfWeek daysOfWeek;

    int position;

    public SlotPosition(){}

    public SlotPosition(Slot slot, DaysOfWeek daysOfWeek, int position){
        this.slot = slot;
        this.daysOfWeek = daysOfWeek;
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

    @Override
    public int compareTo(SlotPosition slotPosition) {
        if(this.daysOfWeek.id>slotPosition.daysOfWeek.id){
            return 1;
        }
        if(this.daysOfWeek.id<slotPosition.daysOfWeek.id){
            return -1;
        }
        if(this.daysOfWeek.id==slotPosition.daysOfWeek.id){
            if(this.position>slotPosition.position){
                return 1;
            }
            if(this.position<slotPosition.position){
                return -1;
            }
        }
        return 0;
    }
}
