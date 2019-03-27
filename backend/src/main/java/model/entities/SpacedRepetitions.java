package model.entities;

import javax.persistence.*;

@Entity
public class SpacedRepetitions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    TaskMapper taskMapper;

    @ManyToOne(fetch = FetchType.LAZY)
    RepetitionPlan repetitionPlan;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public RepetitionPlan getRepetitionPlan() {
        return repetitionPlan;
    }
    public void setRepetitionPlan(RepetitionPlan repetitionPlan) {
        this.repetitionPlan = repetitionPlan;
    }

    public TaskMapper getTaskMapper() {
        return taskMapper;
    }
    public void setTaskMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }
}
