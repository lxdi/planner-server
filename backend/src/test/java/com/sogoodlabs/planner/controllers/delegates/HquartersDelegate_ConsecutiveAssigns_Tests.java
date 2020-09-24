package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.ATestsWithTargetsMeansQuartalsGenerated;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class HquartersDelegate_ConsecutiveAssigns_Tests extends ATestsWithTargetsMeansQuartalsGenerated {

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

    @Before
    public void init(){
        super.init();

        hQuarter1 = ihQuarterDAO.getHQuartersInYear(2018).get(0);
        hQuarter2 = ihQuarterDAO.getHQuartersInYear(2018).get(3);

        //----
        slot1 = new Slot(hQuarter1, 1);
        slotDAO.saveOrUpdate(slot1);

        slotPosition11 = new SlotPosition(slot1, DaysOfWeek.mon, 1);
        slotDAO.saveOrUpdate(slotPosition11);

        slotPosition12 = new SlotPosition(slot1, DaysOfWeek.fri, 3);
        slotDAO.saveOrUpdate(slotPosition12);

        slotPosition13 = new SlotPosition(slot1, DaysOfWeek.sun, 2);
        slotDAO.saveOrUpdate(slotPosition13);

        //----
        slot2 = new Slot(hQuarter2, 1);
        slotDAO.saveOrUpdate(slot2);

        slotPosition21 = new SlotPosition(slot2, DaysOfWeek.tue, 1);
        slotDAO.saveOrUpdate(slotPosition21);

        slotPosition22 = new SlotPosition(slot2, DaysOfWeek.fri, 1);
        slotDAO.saveOrUpdate(slotPosition22);

        slotPosition23 = new SlotPosition(slot2, DaysOfWeek.fri, 2);
        slotDAO.saveOrUpdate(slotPosition23);

        //----
        mean = new Mean("test mean", realm);
        meansDAO.saveOrUpdate(mean);

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

    }

    @Test
    public void createTaskMappersTest(){

        hquartersDelegate.assign(mean.getId(), slot1.getId());

        assertTrue(slotDAO.getById(slot1.getId()).getId()==layerDAO.getLayerAtPriority(mean, 1).getId());

        List<Week> weeks = weekDAO.weeksOfHquarter(hQuarter1);

        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 1")), weeks.get(0), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2")), weeks.get(0), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 3")), weeks.get(0), slotPosition13);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 4")), weeks.get(1), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 5")), weeks.get(1), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 6")), weeks.get(1), slotPosition13);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 7")), weeks.get(2), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 8")), weeks.get(2), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 9")), weeks.get(2), slotPosition13);

    }

    @Test
    public void createTaskMappersTwiceTest(){

        hquartersDelegate.assign(mean.getId(), slot1.getId());
        hquartersDelegate.assign(mean.getId(), slot2.getId());

        assertTrue(slotDAO.getById(slot1.getId()).getLayer().getId()==layerDAO.getLayerAtPriority(mean, 1).getId());
        assertTrue(slotDAO.getById(slot2.getId()).getId()==layerDAO.getLayerAtPriority(mean, 2).getId());

        List<Week> weeksHq1 = weekDAO.weeksOfHquarter(hQuarter1);
        List<Week> weeksHq2 = weekDAO.weeksOfHquarter(hQuarter2);

        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 1")), weeksHq1.get(0), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2")), weeksHq1.get(0), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 3")), weeksHq1.get(0), slotPosition13);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 4")), weeksHq1.get(1), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 5")), weeksHq1.get(1), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 6")), weeksHq1.get(1), slotPosition13);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 7")), weeksHq1.get(2), slotPosition11);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 8")), weeksHq1.get(2), slotPosition12);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 9")), weeksHq1.get(2), slotPosition13);

        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-1")), weeksHq2.get(0), slotPosition21);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-2")), weeksHq2.get(0), slotPosition22);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-3")), weeksHq2.get(0), slotPosition23);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-4")), weeksHq2.get(1), slotPosition21);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-5")), weeksHq2.get(1), slotPosition22);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-6")), weeksHq2.get(1), slotPosition23);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-7")), weeksHq2.get(2), slotPosition21);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-8")), weeksHq2.get(2), slotPosition22);
        checkTaskMapper(taskMappersDAO.taskMapperForTask(tasksDAO.byTitle("task 2-9")), weeksHq2.get(2), slotPosition23);

    }

    private void checkTaskMapper(TaskMapper taskMapper, Week week, SlotPosition slotPosition){
        assertTrue(taskMapper.getSlotPosition().getId()==slotPosition.getId());
        assertTrue(taskMapper.getWeek().getId()==week.getId());
    }

    private Task createTask(String title, Subject subject, int position){
        Task task = new Task(title, subject, position);
        tasksDAO.saveOrUpdate(task);
        return task;
    }

}
