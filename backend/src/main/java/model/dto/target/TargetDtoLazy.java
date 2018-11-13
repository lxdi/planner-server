package model.dto.target;

/**
 * Created by Alexander on 10.03.2018.
 */
public class TargetDtoLazy extends AbstractTargetDto{

    Long parentid;
    Long realmid;

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
}
