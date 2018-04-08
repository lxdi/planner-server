package model.dto.mean;

import model.entities.Mean;

/**
 * Created by Alexander on 06.03.2018.
 */
public abstract class AbstractMeanDto {

    long id;
    String title;
    String criteria;

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

    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

}
