package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.ATestsWithTargetsMeansQuartalsGenerated;

import java.util.List;

import static junit.framework.TestCase.assertTrue;


@Transactional
public class HquartersDelegate_Unassigns_Tests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    HquartersDelegate hquartersDelegate;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    private ISlotPositionDAO slotPositionDAO;

    Mean mean;
    HQuarter hQuarter1;
    Slot slot1;
    SlotPosition slotPosition11;
    SlotPosition slotPosition12;
    SlotPosition slotPosition13;

    HQuarter hQuarter2;
    Slot slot2;
    SlotPosition slotPosition21;
    SlotPosition slotPosition22;
    SlotPosition slotPosition23;

    HQuarter hQuarter3;
    Slot slot3;
    SlotPosition slotPosition31;
    SlotPosition slotPosition32;
    SlotPosition slotPosition33;

    @Before
    public void init(){
        super.init();

        hQuarter1 = ihQuarterDAO.getHQuartersInYear(2018).get(0);
        hQuarter2 = ihQuarterDAO.getHQuartersInYear(2018).get(3);
        hQuarter3 = ihQuarterDAO.getHQuartersInYear(2018).get(7);

        //----
        slot1 = new Slot(hQuarter1, 1);
        slotDAO.save(slot1);

        slotPosition11 = new SlotPosition(slot1, DaysOfWeek.mon, 1);
        slotPositionDAO.save(slotPosition11);

        slotPosition12 = new SlotPosition(slot1, DaysOfWeek.fri, 3);
        slotPositionDAO.save(slotPosition12);

        slotPosition13 = new SlotPosition(slot1, DaysOfWeek.sun, 2);
        slotPositionDAO.save(slotPosition13);

        //----
        slot2 = new Slot(hQuarter2, 1);
        slotDAO.save(slot2);

        slotPosition21 = new SlotPosition(slot2, DaysOfWeek.tue, 1);
        slotPositionDAO.save(slotPosition21);

        slotPosition22 = new SlotPosition(slot2, DaysOfWeek.fri, 1);
        slotPositionDAO.save(slotPosition22);

        slotPosition23 = new SlotPosition(slot2, DaysOfWeek.fri, 2);
        slotPositionDAO.save(slotPosition23);

        //------
        slot3 = new Slot(hQuarter3, 1);
        slotDAO.save(slot3);

        slotPosition31 = new SlotPosition(slot3, DaysOfWeek.mon, 3);
        slotPositionDAO.save(slotPosition31);

        slotPosition32 = new SlotPosition(slot3, DaysOfWeek.thu, 1);
        slotPositionDAO.save(slotPosition32);

        slotPosition33 = new SlotPosition(slot3, DaysOfWeek.fri, 1);
        slotPositionDAO.save(slotPosition33);

        //----
        mean = new Mean("test mean", realm);
        meansDAO.save(mean);

        //--
        Layer layer1 = new Layer(mean, 1);
        layerDAO.saveOrUpdate(layer1);

        Subject subject1 = new Subject(layer1, 1);
        subjectDAO.save(subject1);

        createTask("task 1", subject1, 1);
        createTask("task 2", subject1, 2);
        createTask("task 3", subject1, 3);

        Subject subject2 = new Subject(layer1, 2);
        subjectDAO.save(subject2);

        createTask("task 5", subject2, 2);
        createTask("task 6", subject2, 3);
        createTask("task 4", subject2, 1);
        createTask("task 7", subject2, 4);

        Subject subject3 = new Subject(layer1, 3);
        subjectDAO.save(subject3);

        createTask("task 9", subject3, 2);
        createTask("task 8", subject3, 1);

        Layer layer2 = new Layer(mean, 2);
        layerDAO.saveOrUpdate(layer2);

        //--
        Subject subject21 = new Subject(layer2, 1);
        subject21.setLayer(layer2);
        subjectDAO.save(subject21);

        createTask("task 2-1", subject21, 1);
        createTask("task 2-4", subject21, 4);
        createTask("task 2-5", subject21, 5);
        createTask("task 2-2", subject21, 2);
        createTask("task 2-3", subject21, 3);
        createTask("task 2-6", subject21, 6);
        createTask("task 2-7", subject21, 7);
        createTask("task 2-9", subject21, 9);
        createTask("task 2-8", subject21, 8);

        Layer layer3 = new Layer(mean, 3);
        layerDAO.saveOrUpdate(layer3);

    }

    @Test
    public void unassignTest(){
        hquartersDelegate.assign(mean.getId(), slot1.getId());
        hquartersDelegate.assign(mean.getId(), slot2.getId());
        hquartersDelegate.assign(mean.getId(), slot3.getId());

        hquartersDelegate.unassign(slot1.getId());

        assertTrue(slotDAO.getOne(slot1.getId()).getMean()==null);
        assertTrue(slotDAO.getOne(slot1.getId()).getLayer()==null);

        assertTrue(slotDAO.getOne(slot2.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getOne(slot2.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 1).getId());

        assertTrue(slotDAO.getOne(slot3.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getOne(slot3.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 2).getId());

        List<Week> weeksHq2 = weekDAO.weeksOfHquarter(hQuarter2);
        List<Week> weeksHq3 = weekDAO.weeksOfHquarter(hQuarter3);

        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 1")), weeksHq2.get(0), slotPosition21);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2")), weeksHq2.get(0), slotPosition22);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 3")), weeksHq2.get(0), slotPosition23);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 4")), weeksHq2.get(1), slotPosition21);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 5")), weeksHq2.get(1), slotPosition22);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 6")), weeksHq2.get(1), slotPosition23);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 7")), weeksHq2.get(2), slotPosition21);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 8")), weeksHq2.get(2), slotPosition22);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 9")), weeksHq2.get(2), slotPosition23);

        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-1")), weeksHq3.get(0), slotPosition31);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-2")), weeksHq3.get(0), slotPosition32);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-3")), weeksHq3.get(0), slotPosition33);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-4")), weeksHq3.get(1), slotPosition31);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-5")), weeksHq3.get(1), slotPosition32);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-6")), weeksHq3.get(1), slotPosition33);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-7")), weeksHq3.get(2), slotPosition31);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-8")), weeksHq3.get(2), slotPosition32);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-9")), weeksHq3.get(2), slotPosition33);

    }

    @Test
    public void unassignMiddleSlotTest(){
        hquartersDelegate.assign(mean.getId(), slot1.getId());
        hquartersDelegate.assign(mean.getId(), slot2.getId());
        hquartersDelegate.assign(mean.getId(), slot3.getId());

        hquartersDelegate.unassign(slot2.getId());

        assertTrue(slotDAO.getOne(slot1.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getOne(slot1.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 1).getId());


        assertTrue(slotDAO.getOne(slot2.getId()).getMean()==null);
        assertTrue(slotDAO.getOne(slot2.getId()).getLayer()==null);

        assertTrue(slotDAO.getOne(slot3.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getOne(slot3.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 2).getId());

        List<Week> weeksHq1 = weekDAO.weeksOfHquarter(hQuarter1);
        List<Week> weeksHq3 = weekDAO.weeksOfHquarter(hQuarter3);

        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 1")), weeksHq1.get(0), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2")), weeksHq1.get(0), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 3")), weeksHq1.get(0), slotPosition13);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 4")), weeksHq1.get(1), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 5")), weeksHq1.get(1), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 6")), weeksHq1.get(1), slotPosition13);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 7")), weeksHq1.get(2), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 8")), weeksHq1.get(2), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 9")), weeksHq1.get(2), slotPosition13);

        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-1")), weeksHq3.get(0), slotPosition31);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-2")), weeksHq3.get(0), slotPosition32);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-3")), weeksHq3.get(0), slotPosition33);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-4")), weeksHq3.get(1), slotPosition31);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-5")), weeksHq3.get(1), slotPosition32);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-6")), weeksHq3.get(1), slotPosition33);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-7")), weeksHq3.get(2), slotPosition31);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-8")), weeksHq3.get(2), slotPosition32);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.findByTitle("task 2-9")), weeksHq3.get(2), slotPosition33);

    }

    private void checkTaskMapper(TaskMapper taskMapper, Week week, SlotPosition slotPosition){
        assertTrue(taskMapper.getSlotPosition().getId()==slotPosition.getId());
        assertTrue(taskMapper.getWeek().getId()==week.getId());
    }

    private Task createTask(String title, Subject subject, int position){
        Task task = new Task(title, subject, position);
        tasksDAO.save(task);
        return task;
    }

}
