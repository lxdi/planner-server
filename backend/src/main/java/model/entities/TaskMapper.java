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

}
