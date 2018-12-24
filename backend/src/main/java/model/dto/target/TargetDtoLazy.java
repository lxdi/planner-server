package model.dto.target;

/**
 * Created by Alexander on 10.03.2018.
 */
public class TargetDtoLazy {

    long id;
    String title;

    Long parentid;
    Long realmid;
    Long nextid;
    Long previd;


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

    public Long getParentid() {
        return parentid;
    }
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public Long getRealmid() {
        return realmid;
    }
    public void setRealmid(Long realmid) {
        this.realmid = realmid;
    }

    public Long getNextid() {
        return nextid;
    }
    public void setNextid(Long nextid) {
        this.nextid = nextid;
    }

    public Long getPrevid() {
        return previd;
    }
    public void setPrevid(Long previd) {
        this.previd = previd;
    }
}
