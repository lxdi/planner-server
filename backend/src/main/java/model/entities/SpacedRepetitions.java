package model.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class SpacedRepetitions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    TaskMapper taskMapper;

    @ManyToOne
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
