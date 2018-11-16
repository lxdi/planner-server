package model.dto.mean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 08.04.2018.
 */
public class MeanDtoLazy extends AbstractMeanDto {

    Long parentid;
    List<Long> targetsIds = new ArrayList<>();
    Long realmid;

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
}
