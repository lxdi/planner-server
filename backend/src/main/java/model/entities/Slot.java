package model.entities;

import javax.persistence.*;

@Entity
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    int position;

    @ManyToOne(fetch = FetchType.LAZY)
    Mean mean;

    @ManyToOne(fetch = FetchType.LAZY)
    Layer layer;

    @ManyToOne(fetch = FetchType.LAZY)
    HQuarter hquarter;

    public Slot(){}

    public Slot(HQuarter hquarter, int position){
        assert hquarter!=null && position>0;
        this.hquarter = hquarter;
        this.position = position;
    }

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }
}
