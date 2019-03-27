package model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    int priority;
    boolean done;

    @ManyToOne(fetch = FetchType.LAZY)
    Mean mean;

    @OneToMany(mappedBy = "layer", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Subject> subjects = new ArrayList();

    public Layer(){}

    public Layer(Mean mean, int priority){
        assert mean!=null && priority>0;
        this.mean = mean;
        this.priority = priority;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Mean getMean() {
        return mean;
    }
    public void setMean(Mean mean) {
        this.mean = mean;
    }

    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
