package com.sogoodlabs.planner.model.dto.hquarter;

import com.sogoodlabs.planner.controllers.delegates.HquartersDelegate;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.dto.HquarterMapper;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.ATestsWithTargetsMeansQuartalsGenerated;
import com.sogoodlabs.planner.test_configs.WeeksMappingChecker;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class HquarterDtoLazyMapper_WeeksMappingTests extends ATestsWithTargetsMeansQuartalsGenerated {

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
    IWeekDAO weekDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    @Autowired
    private ISlotPositionDAO slotPositionDAO;

    Mean mean;
    HQuarter hQuarter1;
    Slot slot1;
    SlotPosition slotPosition11;
    SlotPosition slotPosition12;
    SlotPosition slotPosition13;

    Slot slot2;
    SlotPosition slotPosition21;
    SlotPosition slotPosition22;
    SlotPosition slotPosition23;

    @Before
    public void init(){
        super.init();

        hQuarter1 = ihQuarterDAO.getHQuartersInYear(2018).get(0);

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
        slot2 = new Slot(hQuarter1, 1);
        slotDAO.save(slot2);

        slotPosition21 = new SlotPosition(slot2, DaysOfWeek.tue, 1);
        slotPositionDAO.save(slotPosition21);

        slotPosition22 = new SlotPosition(slot2, DaysOfWeek.fri, 1);
        slotPositionDAO.save(slotPosition22);

        slotPosition23 = new SlotPosition(slot2, DaysOfWeek.fri, 2);
        slotPositionDAO.save(slotPosition23);

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

        hquartersDelegate.assign(mean.getId(), slot1.getId());

    }

    @Test
    public void weeksDtoWithTasksMappingTest(){
        Map<String, Object> hquarterDtoFull = hquarterMapper.mapToDtoFull(hQuarter1);
        assertTrue(((List)hquarterDtoFull.get("weeks")).size()==4);
        Stack<String> tasksTitles = new Stack<>();
        tasksTitles.push("task 9");
        tasksTitles.push("task 8");
        tasksTitles.push("task 7");
        tasksTitles.push("task 6");
        tasksTitles.push("task 5");
        tasksTitles.push("task 4");
        tasksTitles.push("task 3");
        tasksTitles.push("task 2");
        tasksTitles.push("task 1");

        List<Map<String, Object>> weeks = (List<Map<String, Object>>) hquarterDtoFull.get("weeks");

        //check weeks
        for (int i = 0; i < 3; i++) {
            Map<DaysOfWeek, List<String>> mappingWeek = new HashMap();
            mappingWeek.put(DaysOfWeek.mon, new ArrayList<>(Arrays.asList(tasksTitles.pop())));
            mappingWeek.put(DaysOfWeek.fri, new ArrayList<>(Arrays.asList(tasksTitles.pop())));
            mappingWeek.put(DaysOfWeek.sun, new ArrayList<>(Arrays.asList(tasksTitles.pop())));
            WeeksMappingChecker.checkWeek(mappingWeek, weeks, i);
        }
    }


    private Task createTask(String title, Subject subject, int position){
        Task task = new Task(title, subject, position);
        tasksDAO.saveOrUpdate(task);
        return task;
    }



}
