package model.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Mean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;
    String criteria;
    @OneToMany
    @JoinColumn(name="mean_target")
    List<Target> targets;
    @ManyToOne
    Mean parent;
    @OneToMany(mappedBy = "parent")
    List<Mean> children;


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Mean getParent() {
        return parent;
    }
    public void setParent(Mean parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public List<Target> getTargets() {
        return targets;
    }
    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public List<Mean> getChildren() {
        return children;
    }
    public void setChildren(List<Mean> children) {
        this.children = children;
    }
}
