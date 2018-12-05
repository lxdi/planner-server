package model.entities;

import javax.persistence.*;

@Entity
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    long position;

    @ManyToOne
    Mean mean;

    @ManyToOne
    HQuarter hquarter;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Mean getMean() {
        return mean;
    }
    public void setMean(Mean mean) {
        this.mean = mean;
    }

    public HQuarter getHquarter() {
        return hquarter;
    }
    public void setHquarter(HQuarter hquarter) {
        this.hquarter = hquarter;
    }

    public long getPosition() {
        return position;
    }
    public void setPosition(long position) {
        this.position = position;
    }
}
