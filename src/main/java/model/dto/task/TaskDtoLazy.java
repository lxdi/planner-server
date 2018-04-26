package model.dto.task;

/**
 * Created by Alexander on 26.04.2018.
 */
public class TaskDtoLazy extends AbstractTaskDto {

    Long meanid;
    Long weekid;

    public Long getMeanid() {
        return meanid;
    }

    public void setMeanid(Long meanid) {
        this.meanid = meanid;
    }

    public Long getWeekid() {
        return weekid;
    }

    public void setWeekid(Long weekid) {
        this.weekid = weekid;
    }
}
