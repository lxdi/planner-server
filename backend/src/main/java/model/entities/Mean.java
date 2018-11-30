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
    Realm realm;

    @ManyToOne
    Quarter quarter;
    private Integer position;

    @OneToMany(mappedBy = "mean", cascade = CascadeType.REMOVE)
    private List<Layer> layers = new ArrayList();

    public Mean(){}

    public Mean(String title, Realm realm){
        assert title!=null && !title.trim().equals("") && realm!=null;
        this.realm = realm;
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

    public Quarter getQuarter() {
        return quarter;
    }
    public void setQuarter(Quarter quarter) {
        this.quarter = quarter;
    }

    public Realm getRealm() {
        return realm;
    }
    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public Integer getPosition() {
        return position;
    }
    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<Layer> getLayers() {
        return layers;
    }
    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    //    public List<Mean> getChildren() {
//        return children;
//    }
//    public void setChildren(List<Mean> children) {
//        this.children = children;
//    }
}
