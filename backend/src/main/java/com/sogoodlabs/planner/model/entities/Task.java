package com.sogoodlabs.planner.model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Task{

    @Id
    String id;
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    Layer layer;

    int position;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Layer getLayer() {
        return layer;
    }
    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

}
