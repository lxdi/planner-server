package model.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;
    @OneToOne
    Mean mean;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Mean getMean() {
        return mean;
    }
    public void setMean(Mean mean) {
        this.mean = mean;
    }
}
