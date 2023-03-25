package com.sogoodlabs.planner;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestCreators {

    @Autowired
    private IRealmDAO realmDAO;

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private ISlotDAO slotDAO;

    public Realm createRealm(){
        var realm = new Realm();
        realm.setId(UUID.randomUUID().toString());
        realmDAO.save(realm);
        return realm;
    }

    public Mean createMean(String title){
        return createMean(title, null);
    }

    public Mean createMean(String title, Realm realm){
        Mean mean = new Mean();
        mean.setId(UUID.randomUUID().toString());
        mean.setTitle(title);
        mean.setRealm(realm);
        meansDAO.save(mean);
        return mean;
    }

    public Layer createLayer(int depth, Mean mean) {
        return createLayer(depth, mean, 0);
    }

    public Layer createLayer(int depth, Mean mean, int priority){
        Layer layer = new Layer();
        layer.setId(UUID.randomUUID().toString());
        layer.setDepth(depth);
        layer.setMean(mean);
        layer.setPriority(priority);
        layerDAO.save(layer);
        return layer;
    }

    public Task createTask(String title, Layer layer){
        return createTask(title, layer, null);
    }

    public Task createTask(String title, Layer layer, RepetitionPlan repPlan){
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(title);
        task.setLayer(layer);
        task.setRepetitionPlan(repPlan);
        tasksDAO.save(task);
        return task;
    }

    public TaskTesting createTesting(String question, Task task){
        TaskTesting testing = new TaskTesting();
        testing.setId(UUID.randomUUID().toString());
        testing.setQuestion(question);
        testing.setTask(task);
        taskTestingDAO.save(testing);
        return testing;
    }

    public Slot createSlot(Realm realm, int hours, DaysOfWeek daysOfWeek) {
        Slot slot = new Slot();
        slot.setId(UUID.randomUUID().toString());
        slot.setRealm(realm);
        slot.setHours(hours);
        slot.setDayOfWeek(daysOfWeek != null? daysOfWeek: DaysOfWeek.mon);
        slotDAO.save(slot);
        return slot;
    }

}
