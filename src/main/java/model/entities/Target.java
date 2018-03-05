package model.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */

@Entity
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    @ManyToOne
    private Target parent;
    @OneToMany(mappedBy = "parent")
    private List<Target> children;

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

    public List<Target> getChildren() {
        return children;
    }
    public void setChildren(List<Target> children) {
        this.children = children;
    }
}
