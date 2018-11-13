package model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Quartal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "quartal")
    List<Mean> means = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public List<Mean> getMeans() {
        return means;
    }
    public void setMeans(List<Mean> means) {
        this.means = means;
    }
}
