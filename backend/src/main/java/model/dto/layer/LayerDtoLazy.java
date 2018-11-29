package model.dto.layer;

import model.dto.subject.SubjectDtoLazy;
import model.entities.Subject;

import java.util.ArrayList;
import java.util.List;

public class LayerDtoLazy {
    long id;
    Long meanid;
    Integer priority;
    boolean done = false;
    List<SubjectDtoLazy> subjects = new ArrayList<>();

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Long getMeanid() {
        return meanid;
    }
    public void setMeanid(Long meanid) {
        this.meanid = meanid;
    }

    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

    public List<SubjectDtoLazy> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<SubjectDtoLazy> subjects) {
        this.subjects = subjects;
    }
}
