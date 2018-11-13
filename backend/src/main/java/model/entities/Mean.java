package model.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
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

    @Fetch(FetchMode.SUBSELECT) // TODO duplicates was added in targets
    @ManyToMany(fetch = FetchType.EAGER)//(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "mean_target",
            joinColumns = @JoinColumn(name = "mean_id"),
            inverseJoinColumns = @JoinColumn(name = "target_id")
    )
    List<Target> targets = new ArrayList<>();

    @ManyToOne
    Mean parent;
//    @OneToMany(mappedBy = "parent")
//    List<Mean> children;

    @ManyToOne
    Quartal quartal;

    public Mean(){}

    public Mean(String title){
        this.title = title;
    }

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

    public Quartal getQuartal() {
        return quartal;
    }
    public void setQuartal(Quartal quartal) {
        this.quartal = quartal;
    }

    //    public List<Mean> getChildren() {
//        return children;
//    }
//    public void setChildren(List<Mean> children) {
//        this.children = children;
//    }
}
