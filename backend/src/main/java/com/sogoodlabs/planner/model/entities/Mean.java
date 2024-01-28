package com.sogoodlabs.planner.model.entities;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.common_mapper.annotations.MapToClass;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
@Table(name = "means")
public class Mean {

    @Id
    String id;
    String title;
    String criteria;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next")
    Mean next;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    Mean parent;
//    @OneToMany(mappedBy = "parent")
//    List<Mean> children;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realm")
    Realm realm;

    @Column(name = "hide_children")
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

    @IncludeInDto
    public List<Layer> getLayers() {
        return layers;
    }

    @MapToClass(value = Layer.class, parentField = "mean")
    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }


}
