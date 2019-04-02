package controllers.delegates;

import model.dao.IHQuarterDAO;
import model.dao.ITaskMappersDAO;
import model.dao.IWeekDAO;
import model.entities.*;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import services.QuarterGenerator;
import test_configs.SpringTestConfig;
import test_configs.TestCreators;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class TasksMappersServiceTests extends SpringTestConfig {

    @Autowired
    TestCreators testCreators;

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    TaskMappersService taskMappersService;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    SessionFactory sessionFactory;

    Mean mean;
    Layer layer;
    Subject subject;
    HQuarter hQuarter;
    Slot slot;
    List<SlotPosition> slotPositions;
    List<Week> weeks;

    @Before
    public void init(){
        mean = testCreators.createMean(null);
        layer = testCreators.createLayer(mean);
        subject = testCreators.createSubject(layer);

        quarterGenerator.generateYear(2019);
        hQuarter = ihQuarterDAO.getHQuartersInYear(2019).get(3);
        sessionFactory.getCurrentSession().getTransaction().commit();

        slot = testCreators.createSlot(layer, mean, hQuarter);
        slotPositions = new ArrayList<>();

        slotPositions.add(testCreators.createSlotPosition(slot, DaysOfWeek.mon, 1));
        slotPositions.add(testCreators.createSlotPosition(slot, DaysOfWeek.fri, 3));
        slotPositions.add(testCreators.createSlotPosition(slot, DaysOfWeek.sat, 1));

        weeks = weekDAO.weeksOfHquarter(hQuarter);

    }

    @Test
    public void createTaskMappersTest(){
        final int numberOfTasks = 6;
        List<Task> tasks = new ArrayList<>();
        for(int i = 0; i<numberOfTasks;i++){
            tasks.add(testCreators.createTask(subject));
        }

        Map<Long, List<Long>> exclusions = new HashMap<>();
        List<Long> spIds = new ArrayList<>();
        slotPositions.forEach(sp -> spIds.add(sp.getId()));
        exclusions.put(weeks.get(0).getId(), spIds);

        taskMappersService.createTaskMappers(layer, slot, exclusions);

        List<TaskMapper> taskMappers = taskMappersDAO.taskMappersByWeeksAndSlotPositions(
                weeks, Arrays.asList(slotPositions.get(0), slotPositions.get(1), slotPositions.get(2))
        );

        assertTrue(taskMappers.size()==numberOfTasks);
        taskMappers.forEach(taskmapper -> assertTrue(taskmapper.getWeek().getId()!=weeks.get(0).getId()));

    }

    @Test
    public void createTaskMappersTest_2(){
        final int numberOfTasks = 9;
        List<Task> tasks = new ArrayList<>();
        for(int i = 0; i<numberOfTasks;i++){
            tasks.add(testCreators.createTask(subject));
        }

        Map<Long, List<Long>> exclusions = new HashMap<>();
        List<Long> spIds = new ArrayList<>();
        slotPositions.forEach(sp -> spIds.add(sp.getId()));
        exclusions.put(weeks.get(0).getId(), spIds);

        taskMappersService.createTaskMappers(layer, slot, exclusions);

        List<TaskMapper> taskMappers = taskMappersDAO.taskMappersByWeeksAndSlotPositions(
                weeks, Arrays.asList(slotPositions.get(0), slotPositions.get(1), slotPositions.get(2))
        );

        assertTrue(taskMappers.size()==numberOfTasks);
        taskMappers.forEach(taskmapper -> assertTrue(taskmapper.getWeek().getId()!=weeks.get(0).getId()));

        assertTrue(taskMappersDAO.taskMapperForTask(tasks.get(0)).getSlotPosition().getId()==slotPositions.get(0).getId());
        assertTrue(taskMappersDAO.taskMapperForTask(tasks.get(0)).getWeek().getId()==weeks.get(1).getId());

        checkTaskMapper(tasks, weeks, slotPositions, 0, 0, 1);
        checkTaskMapper(tasks, weeks, slotPositions, 1, 1, 1);
        checkTaskMapper(tasks, weeks, slotPositions, 2, 2, 1);
        checkTaskMapper(tasks, weeks, slotPositions, 3, 0, 2);
        checkTaskMapper(tasks, weeks, slotPositions, 4, 1, 2);
        checkTaskMapper(tasks, weeks, slotPositions, 5, 2, 2);
        checkTaskMapper(tasks, weeks, slotPositions, 6, 0, 3);
        checkTaskMapper(tasks, weeks, slotPositions, 7, 1, 3);
        checkTaskMapper(tasks, weeks, slotPositions, 8, 2, 3);

    }

    private void checkTaskMapper(List<Task> tasks, List<Week> weeks, List<SlotPosition> slotPositions, int itask,  int isp, int iw){
        assertTrue(taskMappersDAO.taskMapperForTask(tasks.get(itask)).getSlotPosition().getId()==slotPositions.get(isp).getId());
        assertTrue(taskMappersDAO.taskMapperForTask(tasks.get(itask)).getWeek().getId()==weeks.get(iw).getId());
    }

}
