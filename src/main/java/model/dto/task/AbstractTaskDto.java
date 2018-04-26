package model.dto.task;

/**
 * Created by Alexander on 26.04.2018.
 */
public abstract class AbstractTaskDto {

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
}
