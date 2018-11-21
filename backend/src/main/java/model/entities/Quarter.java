package model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Quarter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    int year;

    int startMonth;
    int startDay;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "quarter")
//    List<Mean> means = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

//    public List<Mean> getMeans() {
//        return means;
//    }
//    public void setMeans(List<Mean> means) {
//        this.means = means;
//    }

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
