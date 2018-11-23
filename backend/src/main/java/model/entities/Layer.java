package model.entities;

import javax.persistence.*;

@Entity
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    int priority;

    @OneToOne
    @JoinColumn(name="mean")
    Mean mean;

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
}
