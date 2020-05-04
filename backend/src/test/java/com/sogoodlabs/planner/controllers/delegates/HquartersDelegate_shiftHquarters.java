package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.ATestsWithTargetsMeansQuartalsGenerated;
import com.sogoodlabs.planner.test_configs.TestCreatorsAnotherSession;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Transactional
public class HquartersDelegate_shiftHquarters extends ATestsWithTargetsMeansQuartalsGenerated {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    HquartersDelegate hquartersDelegate;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    TestCreatorsAnotherSession testCreators;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    TaskMappersService taskMappersService;

    @Test
    public void shiftHquartersBasicTest(){

        List<HQuarter> beforeShifting = hquarterDAO.getHQuartersInYear(2018);

        Map<Long, Week> oldWeeks = new HashMap<>();
        beforeShifting.forEach(hq -> oldWeeks.put(hq.getId(), hq.getStartWeek()));

        HQuarter selectedHquarter = beforeShifting.get(5);

        hquartersDelegate.shiftHquarters(selectedHquarter.getId());

        List<HQuarter> hQuartersAfterShifting = hquarterDAO.getHQuartersInYear(2018);

        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 2, 0, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 3, 0, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 4, 0, oldWeeks);

        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 5, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 6, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 7, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 8, 1, oldWeeks);
        checkHquarterShifting(beforeShifting, hQuartersAfterShifting, 9, 1, oldWeeks);

    }

    @Test
    public void shiftHqaurtersTest(){
        List<HQuarter> beforeShifting = hquarterDAO.getHQuartersInYear(2018);
        HQuarter selectedHquarter = beforeShifting.get(6);
        Week oldStartWeek = selectedHquarter.getStartWeek();

        entityManager.unwrap(Session.class).getTransaction().commit();
        entityManager.unwrap(Session.class).beginTransaction();

        Realm realm = testCreators.createRealm();
        Mean mean = testCreators.createMean(realm);
        Layer layer = testCreators.createLayer(mean);
        Subject subject = testCreators.createSubject(layer);
        Task task = testCreators.createTask(subject);
        Task task2 = testCreators.createTask(subject);
        Task task3 = testCreators.createTask(subject);
        Task task4 = testCreators.createTask(subject);
        Task task5 = testCreators.createTask(subject);
        Task task6 = testCreators.createTask(subject);

        Slot slot = testCreators.createSlot(layer, mean, selectedHquarter);
        SlotPosition slotPosition = testCreators.createSlotPosition(slot, DaysOfWeek.mon, 1);
        SlotPosition slotPosition2 = testCreators.createSlotPosition(slot, DaysOfWeek.wed, 2);
        SlotPosition slotPosition3 = testCreators.createSlotPosition(slot, DaysOfWeek.fri, 2);

        MapperExclusion mapperExclusion = testCreators.createMapperExclusion(selectedHquarter.getStartWeek(), slotPosition2);
        MapperExclusion mapperExclusion2 = testCreators.createMapperExclusion(selectedHquarter.getEndWeek(), slotPosition);

        taskMappersService.rescheduleTaskMappers(mean, true);

        assertTrue(taskMappersDAO.taskMapperForTask(task).getWeek().getId()==selectedHquarter.getStartWeek().getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task2).getWeek().getId()==selectedHquarter.getStartWeek().getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task2).getSlotPosition().getId()==slotPosition3.getId());

        hquartersDelegate.shiftHquarters(selectedHquarter.getId());

        assertTrue(selectedHquarter.getStartWeek().getId()!=oldStartWeek.getId());

        assertTrue(taskMappersDAO.taskMapperForTask(task).getWeek().getId()==selectedHquarter.getStartWeek().getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task).getSlotPosition().getId()==slotPosition.getId());

        assertTrue(taskMappersDAO.taskMapperForTask(task2).getWeek().getId()==selectedHquarter.getStartWeek().getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task2).getSlotPosition().getId()==slotPosition2.getId());

        assertTrue(taskMappersDAO.taskMapperForTask(task3).getWeek().getId()==selectedHquarter.getStartWeek().getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task3).getSlotPosition().getId()==slotPosition3.getId());

        assertTrue(taskMappersDAO.taskMapperForTask(task4).getWeek().getId()
                == weekDAO.weekByYearAndNumber(2018, selectedHquarter.getStartWeek().getNumber()+1).getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task4).getSlotPosition().getId()==slotPosition.getId());

        assertTrue(taskMappersDAO.taskMapperForTask(task5).getWeek().getId()
                == weekDAO.weekByYearAndNumber(2018, selectedHquarter.getStartWeek().getNumber()+1).getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task5).getSlotPosition().getId()==slotPosition2.getId());

        assertTrue(taskMappersDAO.taskMapperForTask(task6).getWeek().getId()
                == weekDAO.weekByYearAndNumber(2018, selectedHquarter.getStartWeek().getNumber()+1).getId());
        assertTrue(taskMappersDAO.taskMapperForTask(task6).getSlotPosition().getId()==slotPosition3.getId());

    }

    @Test(expected = RuntimeException.class)
    public void shiftHquartersEdgeCaseTest(){

        List<HQuarter> beforeShifting = hquarterDAO.getHQuartersInYear(2019);

        Map<Long, Week> oldWeeks = new HashMap<>();
        beforeShifting.forEach(hq -> oldWeeks.put(hq.getId(), hq.getStartWeek()));

        hquartersDelegate.shiftHquarters(beforeShifting.get(4).getId());
        hquartersDelegate.shiftHquarters(beforeShifting.get(6).getId());
        hquartersDelegate.shiftHquarters(beforeShifting.get(7).getId());
        hquartersDelegate.shiftHquarters(beforeShifting.get(9).getId());

        //now the last week of the last hquarter equals the last week of year
        // try to shift to the next year and expect an exception
        hquartersDelegate.shiftHquarters(beforeShifting.get(10).getId());

    }



    private void checkHquarterShifting(List<HQuarter> beforeShift, List<HQuarter> afterShift, int i, int isShifted, Map<Long, Week> oldWeeks){
        assertTrue(afterShift.get(i).getStartWeek().getId()
                == weekDAO.weekByYearAndNumber(2018, oldWeeks.get(beforeShift.get(i).getId()).getNumber()+isShifted).getId());
    }

}
