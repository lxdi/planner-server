package model.dto.task;

import model.dto.topic.TopicDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 26.04.2018.
 */
public class TaskDtoLazy {

    long id;
    String title;
    Long subjectid;

    int position;

    List<TopicDto> topics = new ArrayList<>();

    List<Map<String, Object>> testings = new ArrayList<>();

    boolean finished = false;

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

    public Long getSubjectid() {
        return subjectid;
    }
    public void setSubjectid(Long subjectid) {
        this.subjectid = subjectid;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }
    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public boolean getFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public List<Map<String, Object>> getTestings() {
        return testings;
    }
    public void setTestings(List<Map<String, Object>> testings) {
        this.testings = testings;
    }
}
