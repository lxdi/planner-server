package model.dto.mean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeanDtoLazy{

    long id;
    String title;
    Long nextid;

    Long parentid;
    List<Long> targetsIds = new ArrayList<>();
    Long realmid;
    boolean hideChildren = false;


    public Long getParentid() {
        return parentid;
    }
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public List<Long> getTargetsIds() {
        return targetsIds;
    }
    public void setTargetsIds(List<Long> targetsIds) {
        this.targetsIds = targetsIds;
    }

    public Long getRealmid() {
        return realmid;
    }
    public void setRealmid(Long realmid) {
        this.realmid = realmid;
    }

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

    public Long getNextid() {
        return nextid;
    }
    public void setNextid(Long nextid) {
        this.nextid = nextid;
    }

    public boolean isHideChildren() {
        return hideChildren;
    }
    public void setHideChildren(boolean hideChildren) {
        this.hideChildren = hideChildren;
    }
}
