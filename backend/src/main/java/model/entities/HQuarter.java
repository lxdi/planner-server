package model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class HQuarter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    int year;
    int startMonth;
    int startDay;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }


    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public int getStartMonth() {
        return startMonth;
    }
    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartDay() {
        return startDay;
    }
    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }
}
