package model.entities;

import javax.persistence.*;

@Entity
public class TaskMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne
    Task task;

    @OneToOne
    SlotPosition slotPosition;

    @OneToOne
    Week week;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public SlotPosition getSlotPosition() {
        return slotPosition;
    }

    public void setSlotPosition(SlotPosition slotPosition) {
        this.slotPosition = slotPosition;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }
}
