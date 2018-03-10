package model.dto;

import model.entities.Target;

import java.util.List;

/**
 * Created by Alexander on 06.03.2018.
 */
abstract class AbstractTargetDto {

    long id;
    String title;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void fill(Target target){
        this.setId(target.getId());
        this.setTitle(target.getTitle());
    }
}
