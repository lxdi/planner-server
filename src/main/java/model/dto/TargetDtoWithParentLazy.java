package model.dto;

/**
 * Created by Alexander on 10.03.2018.
 */
public class TargetDtoWithParentLazy extends AbstractTargetDto{

    Long parentid;

    public Long getParentid() {
        return parentid;
    }
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }
}
