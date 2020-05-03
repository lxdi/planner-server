package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.entities.*;
import org.springframework.stereotype.Service;
import com.sogoodlabs.planner.services.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public abstract class ATestCreators {

    public Realm createRealm(){
        Realm realm = new Realm();
        save(realm);
        return realm;
    }

    public Target createTarget(Realm realm){
        return createTarget(null, realm);
    }

    public Target createTarget(Target parent, Realm realm){
        Target target = new Target();
        target.setRealm(realm);
        target.setParent(parent);
        save(target);
        return target;
    }

    public Mean createMean(Realm realm){
        return createMean(null, new ArrayList<>(), realm);
    }

    public Mean createMean(Mean parent, List<Target> targets, Realm realm){
        Mean mean = new Mean();
        mean.setParent(parent);
        mean.setRealm(realm);
        mean.setTargets(targets);
        save(mean);
        return mean;
    }

    public Layer createLayer(Mean mean){
        Layer layer = new Layer();
        layer.setMean(mean);
        save(layer);
        return layer;
    }

    public Subject createSubject(Layer layer){
        Subject subject =  new Subject();
        subject.setLayer(layer);
        save(subject);
        return subject;
    }

    public Task createTask(Subject subject){
        Task task =  new Task();
        task.setSubject(subject);
        save(task);
        return task;
    }

    public TaskMapper createTaskMapper(Task task, Week week, SlotPosition slotPosition){
        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMapper.setWeek(week);
        taskMapper.setSlotPosition(slotPosition);
        save(taskMapper);
        return taskMapper;
    }

    public Topic createTopic(Task task){
        Topic topic = new Topic();
        topic.setTask(task);
        save(topic);
        return topic;
    }

    public TaskTesting createTesting(Task task){
        TaskTesting taskTesting = new TaskTesting();
        taskTesting.setTask(task);
        save(taskTesting);
        return taskTesting;
    }

    public Slot createSlot(Layer layer, Mean mean, HQuarter hQuarter){
        Slot slot = new Slot();
        slot.setLayer(layer);
        slot.setMean(mean);
        slot.setHquarter(hQuarter);
        save(slot);
        return slot;
    }

    public SlotPosition createSlotPosition(Slot slot){
        return createSlotPosition(slot, null, 0);
    }

    public SlotPosition createSlotPosition(Slot slot, DaysOfWeek dayOfWeek, int pos){
        SlotPosition slotPosition = new SlotPosition();
        slotPosition.setSlot(slot);
        slotPosition.setDayOfWeek(dayOfWeek);
        slotPosition.setPosition(pos);
        save(slotPosition);
        return slotPosition;
    }

    public HQuarter createHquarter(Week start, Week end){
        HQuarter hQuarter = new HQuarter();
        hQuarter.setStartWeek(start);
        hQuarter.setEndWeek(end);
        save(hQuarter);
        return hQuarter;
    }

    public Week createWeek(Date start, Date end){
        Week week = new Week();
        week.setStartDay(start);
        week.setEndDay(end);
        save(week);
        return week;
    }

    public Week createWeek(String start, String end){
        return createWeek(DateUtils.toDate(start), DateUtils.toDate(end));
    }

    public MapperExclusion createMapperExclusion(Week week, SlotPosition slotPosition){
        MapperExclusion mapperExclusion = new MapperExclusion();
        mapperExclusion.setWeek(week);
        mapperExclusion.setSlotPosition(slotPosition);
        save(mapperExclusion);
        return mapperExclusion;
    }

    public abstract void save(Object object);


    public void save(Object... objs){
        for(Object obj : objs){
            this.save(obj);
        }
    }


}
