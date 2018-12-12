package model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class HQuarter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

//    int year;
//    int startMonth;
//    int startDay;

    @ManyToOne
    Week startWeek;

    @ManyToOne
    Week endWeek;

    boolean custom = false;

    public HQuarter(){}

    public HQuarter(Week startWeek){
        this.startWeek = startWeek;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public boolean isCustom() {
        return custom;
    }
    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public Week getStartWeek() {
        return startWeek;
    }
    public void setStartWeek(Week startWeek) {
        this.startWeek = startWeek;
    }

    public Week getEndWeek() {
        return endWeek;
    }
    public void setEndWeek(Week endWeek) {
        this.endWeek = endWeek;
    }
}
