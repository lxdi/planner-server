package model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 24.02.2018.
 */

@Entity
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;

    @ManyToOne//(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    //@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Target parent;

//    @OneToMany(mappedBy = "parent")
//    private List<Target> children = new ArrayList<>();

    public Target(){
    }

    public Target(String title){
        this.setTitle(title);
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

//    public List<Target> getChildren() {
//        return children;
//    }
//    public void setChildren(List<Target> children) {
//        this.children = children;
//    }
}
