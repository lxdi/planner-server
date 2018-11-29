package model.dto.subject;

import model.dto.task.TaskDtoLazy;
import model.entities.Task;

import java.util.ArrayList;
import java.util.List;

public class SubjectDtoLazy {

    private long id;
    private String title;
    private Long layerid;
    private int position;

    List<TaskDtoLazy> tasks = new ArrayList<>();

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

    public Long getLayerid() {
        return layerid;
    }
    public void setLayerid(Long layerid) {
        this.layerid = layerid;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public List<TaskDtoLazy> getTasks() {
        return tasks;
    }
    public void setTasks(List<TaskDtoLazy> tasks) {
        this.tasks = tasks;
    }
}
