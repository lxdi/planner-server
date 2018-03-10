package model.dto;

/**
 * Created by Alexander on 10.03.2018.
 */
public class TargetDtoWithParentLazy extends AbstractTargetDto{

    Long parent;

    public Long getParent() {
        return parent;
    }
    public void setParent(Long parent) {
        this.parent = parent;
    }
}
