package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.dto.AssignLayerDto;
import com.sogoodlabs.planner.model.dto.AssignMeanDto;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ScheduleMeanServiceTest extends SpringTestConfig {

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ScheduleMeanService scheduleMeanService;

    @Autowired
    private WeeksGenerator weeksGenerator;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private ISlotDAO slotDAO;

    @Autowired
    private IMeansDAO meansDAO;

    @Autowired
    private IRealmDAO realmDAO;

    @BeforeEach
    public void init(){
        super.init();
        weeksGenerator.generateYear(2021);
    }

    @Test
    public void scheduleSingleTest(){
        String dateString = "2021-03-11"; //thu

        var realm = createRealm();
        var mean = createMean(realm);
        var layer = createLayer(mean);
        var task1 = createTask(layer);
        createSlot(realm, DaysOfWeek.fri, 2);


        AssignMeanDto assignMeanDto = new AssignMeanDto();
        assignMeanDto.setStartDayId(dayDao.findByDate(DateUtils.toDate(dateString)).getId());

        AssignLayerDto assignLayerDto = new AssignLayerDto();
        assignLayerDto.getTaskIds().add(task1.getId());
        assignMeanDto.getLayers().add(assignLayerDto);

        scheduleMeanService.schedule(assignMeanDto);

        super.cleanContext();

        task1 = tasksDAO.findById(task1.getId()).get();
        TaskMapper taskMapper = taskMappersDAO.findByTask(task1).get(0);
        assertEquals("2021-03-12", DateUtils.fromDate(taskMapper.getPlanDay().getDate()));

    }

    @Test
    public void scheduleWithPlaceholdersTest(){
        String dateString = "2021-03-11";

        var realm = createRealm();
        var mean = createMean(realm);
        var layer = createLayer(mean);
        var task1 = createTask(layer);

        createSlot(realm, DaysOfWeek.thu, 2);
        createSlot(realm, DaysOfWeek.sat, 2);
        createSlot(realm, DaysOfWeek.mon, 2);

        AssignMeanDto assignMeanDto = new AssignMeanDto();
        assignMeanDto.setStartDayId(dayDao.findByDate(DateUtils.toDate(dateString)).getId());

        AssignLayerDto assignLayerDto = new AssignLayerDto();
        assignLayerDto.getTaskIds().add(task1.getId());
        assignLayerDto.setPlaceholders(2);
        assignLayerDto.setLayerId(layer.getId());
        assignMeanDto.getLayers().add(assignLayerDto);

        scheduleMeanService.schedule(assignMeanDto);

        super.cleanContext();

        task1 = tasksDAO.findById(task1.getId()).get();
        TaskMapper taskMapper = taskMappersDAO.findByTask(task1).get(0);

        List<Task> tasks = tasksDAO.findByLayer(layer);
        Task taskPlaceholder1 = tasks.get(1);
        Task taskPlaceholder2 = tasks.get(2);

        assertEquals(dateString, DateUtils.fromDate(taskMapper.getPlanDay().getDate()));
        assertEquals(3, tasksDAO.findByLayer(layer).size());
        assertEquals("2021-03-13", DateUtils.fromDate(taskMappersDAO.findByTask(taskPlaceholder1).get(0).getPlanDay().getDate()));
        assertEquals("2021-03-15", DateUtils.fromDate(taskMappersDAO.findByTask(taskPlaceholder2).get(0).getPlanDay().getDate()));

    }

    @Test
    public void scheduleOnWeekendTest(){
        String dateString = "2021-03-11";

        Task existingTask = createTask(null);
        TaskMapper existingTaskTM = new TaskMapper();
        existingTaskTM.setId(UUID.randomUUID().toString());
        existingTaskTM.setTask(existingTask);
        existingTaskTM.setPlanDay(dayDao.findByDate(DateUtils.toDate("2021-03-13")));
        taskMappersDAO.save(existingTaskTM);

        var realm = createRealm();
        var mean = createMean(realm);
        var layer = createLayer(mean);
        var task1 = createTask(layer);

        createSlot(realm, DaysOfWeek.thu, 2);
        createSlot(realm, DaysOfWeek.sat, 2);
        createSlot(realm, DaysOfWeek.mon, 2);

        AssignMeanDto assignMeanDto = new AssignMeanDto();
        assignMeanDto.setStartDayId(dayDao.findByDate(DateUtils.toDate(dateString)).getId());

        AssignLayerDto assignLayerDto = new AssignLayerDto();
        assignLayerDto.getTaskIds().add(task1.getId());
        assignLayerDto.setPlaceholders(2);
        assignLayerDto.setLayerId(layer.getId());
        assignMeanDto.getLayers().add(assignLayerDto);

        scheduleMeanService.schedule(assignMeanDto);

        super.cleanContext();

        assertEquals(2, taskMappersDAO.findByPlanDay(dayDao.findByDate(DateUtils.toDate("2021-03-13"))).size());

    }

    private Realm createRealm(){
        var realm = new Realm();
        realm.setId(UUID.randomUUID().toString());
        realmDAO.save(realm);
        return realm;
    }

    private Mean createMean(Realm realm){
        var mean = new Mean();
        mean.setId(UUID.randomUUID().toString());
        mean.setRealm(realm);
        meansDAO.save(mean);
        return mean;
    }
    private Layer createLayer(Mean mean){
        Layer layer = new Layer();
        layer.setId(UUID.randomUUID().toString());
        layer.setMean(mean);
        layerDAO.save(layer);
        return layer;
    }

    private Task createTask(Layer layer){
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setLayer(layer);
        tasksDAO.save(task);
        return task;
    }

    private Slot createSlot(Realm realm, DaysOfWeek daysOfWeek, int hours) {
        var slot = new Slot();
        slot.setId(UUID.randomUUID().toString());
        slot.setHours(hours);
        slot.setRealm(realm);
        slot.setDayOfWeek(daysOfWeek);
        slotDAO.save(slot);
        return slot;
    }
}
