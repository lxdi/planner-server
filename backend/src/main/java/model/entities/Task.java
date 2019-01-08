package model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 05.03.2018.
 */

@Entity
public class Task implements Comparable<Task>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;


    @ManyToOne
    Subject subject;
    int position;

    //@Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Topic> topics = new ArrayList<>();

    public Task(){}

    public Task(String title, Subject subject, int position){
        assert subject!=null && position>0 && title!=null && !title.isEmpty();
        this.title = title;
        this.subject = subject;
        this.position = position;
    }

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

    public Subject getSubject() {
        return subject;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public List<Topic> getTopics() {
        return topics;
    }
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public int compareTo(Task task) {
        if(this.subject.getPosition()>task.subject.getPosition()){
            return 1;
        }
        if(this.subject.getPosition()<task.subject.getPosition()){
            return -1;
        }
        if(this.subject.getPosition()==task.subject.getPosition()){
            if(this.position>task.position){
                return 1;
            }
            if(this.position<task.position){
                return -1;
            }
        }
        return 0;
    }
}
