package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;

/**
 * Created by Alexander on 24.02.2018.
 */

@Entity
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)//(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    //@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Target parent;

    @OneToOne(fetch = FetchType.LAZY)
    Target next;

//    @OneToMany(mappedBy = "parent")
//    private List<Target> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    Realm realm;

    @Column(length = 4096)
    String definitionOfDone;

    public Target(){
    }

    public Target(String title, Realm realm){
        this.setTitle(title);
        this.setRealm(realm);
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Target getParent() {
        return parent;
    }
    public void setParent(Target parent) {
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

    public Target getNext() {
        return next;
    }
    public void setNext(Target next) {
        this.next = next;
    }

    public String getDefinitionOfDone() {
        return definitionOfDone;
    }
    public void setDefinitionOfDone(String definitionOfDone) {
        this.definitionOfDone = definitionOfDone;
    }
}
