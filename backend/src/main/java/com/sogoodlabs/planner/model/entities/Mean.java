package com.sogoodlabs.planner.model.entities;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.common_mapper.annotations.MapToClass;
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
    String id;
    String title;
    String criteria;

    @OneToOne(fetch = FetchType.LAZY)
    Mean next;

    @Fetch(FetchMode.SUBSELECT) // TODO duplicates was added in targets
    @ManyToMany(fetch = FetchType.LAZY)//(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "mean_target",
            joinColumns = @JoinColumn(name = "mean_id"),
            inverseJoinColumns = @JoinColumn(name = "target_id")
    )
    List<Target> targets = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    Mean parent;
//    @OneToMany(mappedBy = "parent")
//    List<Mean> children;

    @ManyToOne(fetch = FetchType.LAZY)
    Realm realm;

    @Column(name = "hidechildren")
    boolean hideChildren = false;

    @Transient
    List<Layer> layers = new ArrayList();

    public String getId() {
        return id;
    }
    public void setId(String id) {
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

    public Realm getRealm() {
        return realm;
    }
    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public boolean getHideChildren() {
        return hideChildren;
    }
    public void setHideChildren(boolean hideChildren) {
        this.hideChildren = hideChildren;
    }

    public Mean getNext() {
        return next;
    }
    public void setNext(Mean next) {
        this.next = next;
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

    @IncludeInDto
    public List<Layer> getLayers() {
        return layers;
    }

    @MapToClass(value = Layer.class)
    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }


}
