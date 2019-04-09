package model.dao;

import model.entities.*;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import services.QuarterGenerator;
import test_configs.AbstractTestsWithTargets;
import services.DateUtils;
import test_configs.TestCreators;

import java.sql.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class TaskMapperDaoTests extends AbstractTestsWithTargets {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    TestCreators testCreators;

    @Autowired
    SessionFactory sessionFactory;

    @Before
    public void init(){
        super.init();
    }

    @Test
    public void taskMapperByTaskTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        assertTrue(taskMappersDAO.taskMapperForTask(task).getId()==taskMapper.getId());
    }

    @Test
    public void getFinishDateTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMapper.setFinishDate(DateUtils.currentDate());
        taskMappersDAO.saveOrUpdate(taskMapper);

        assertTrue(DateUtils.fromDate(taskMappersDAO.finishDateByTaskid(task.getId())).equals(DateUtils.fromDate(DateUtils.currentDate())));
    }

    @Test
    public void getEmptyFinishDateTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        assertTrue(taskMappersDAO.finishDateByTaskid(task.getId())==null);
    }

    @Test
    public void byWeekAndDayTest(){
        SlotPosition slotPosition = new SlotPosition();
        slotPosition.setDayOfWeek(DaysOfWeek.fri);
        slotDAO.saveOrUpdate(slotPosition);

        Week week = new Week();
        weekDAO.saveOrUpdate(week);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setSlotPosition(slotPosition);
        taskMapper.setWeek(week);
        taskMappersDAO.saveOrUpdate(taskMapper);

        List<TaskMapper> taskMappers = taskMappersDAO.byWeekAndDay(week, DaysOfWeek.fri);

        assertTrue(taskMappers.get(0).getId() == taskMapper.getId());

    }

    @Test
    public void taskMappersOfHqAndBeforeTest(){
        quarterGenerator.generateYear(2019);
        HQuarter hQuarter = ihQuarterDAO.getHQuartersInYear(2019).get(3);
        List<Week> weeks = weekDAO.weeksOfHquarter(hQuarter);

        Date date = DateUtils.toDate("2019-04-12");

        sessionFactory.getCurrentSession().getTransaction().commit();

        Slot slot = testCreators.createSlot(null, null, hQuarter);
        SlotPosition slotPosition1 = testCreators.createSlotPosition(slot, DaysOfWeek.mon, 1);
        SlotPosition slotPosition2 = testCreators.createSlotPosition(slot, DaysOfWeek.fri, 1);
        SlotPosition slotPosition3 = testCreators.createSlotPosition(slot, DaysOfWeek.sun, 1);

        TaskMapper taskMapperBefore1 = testCreators.createTaskMapper(null, weeks.get(0), slotPosition2);
        TaskMapper taskMapperBefore2 = testCreators.createTaskMapper(null, weeks.get(0), slotPosition3);
        TaskMapper taskMapperBefore3 = testCreators.createTaskMapper(null, weeks.get(1), slotPosition1);
        TaskMapper taskMapperAfter1 = testCreators.createTaskMapper(null, weeks.get(1), slotPosition2);
        TaskMapper taskMapperAfter2 = testCreators.createTaskMapper(null, weeks.get(1), slotPosition3);
        TaskMapper taskMapperAfter3 = testCreators.createTaskMapper(null, weeks.get(2), slotPosition1);
        TaskMapper taskMapper = testCreators.createTaskMapper(null, weeks.get(2), null);

        List<TaskMapper> taskMappers = taskMappersDAO.taskMappersOfHqAndBefore(hQuarter, date);

        assertTrue(taskMappers.size()==3);

        assertTrue(taskMappers.get(0).getId()==taskMapperBefore1.getId());
        assertTrue(taskMappers.get(1).getId()==taskMapperBefore2.getId());
        assertTrue(taskMappers.get(2).getId()==taskMapperBefore3.getId());

    }
}
