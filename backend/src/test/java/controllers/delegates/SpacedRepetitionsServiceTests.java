package controllers.delegates;

import model.dao.*;
import model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import services.DateUtils;
import services.WeeksGenerator;
import test_configs.SpringTestConfig;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class SpacedRepetitionsServiceTests extends SpringTestConfig {

    @Autowired
    IRepDAO repDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    @Autowired
    SpacedRepetitionsService spacedRepetitionsService;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    WeeksGenerator weeksGenerator;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Test
    public void getActualTaskToRepeatTest(){

        Task task1 = initEntChain(DateUtils.currentDate());
        Task task2 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), 2));
        Task task3 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), -1));

        Task task4 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), -5));

        Task task5 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), 6));

        Topic topicForTask5 = new Topic();
        topicForTask5.setTask(task5);
        topicDAO.saveOrUpdate(topicForTask5);

        Repetition repetitionDone = new Repetition();
        repetitionDone.setPlanDate(DateUtils.addDays(DateUtils.currentDate(), 3));
        repetitionDone.setFactDate(DateUtils.addDays(DateUtils.currentDate(), 4));
        repDAO.save(repetitionDone);

        Task currentTask = new Task();
        tasksDAO.saveOrUpdate(currentTask);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        weeksGenerator.generateYear(currentYear);
        Week currentWeek = weekDAO.weekOfDate(DateUtils.currentDate());
        DaysOfWeek currentDayOfWeek = DaysOfWeek.findById(DateUtils.differenceInDays(currentWeek.getStartDay(), DateUtils.currentDate()));

        SlotPosition slotPosition = new SlotPosition();
        slotPosition.setDaysOfWeek(currentDayOfWeek);
        slotDAO.saveOrUpdate(slotPosition);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setWeek(currentWeek);
        taskMapper.setSlotPosition(slotPosition);
        taskMapper.setTask(currentTask);
        taskMappersDAO.saveOrUpdate(taskMapper);

        Map<Integer, List<Map<String, Object>>> tasks = spacedRepetitionsService.getActualTaskToRepeat();

        assertTrue(tasks.get(-1).size()==1);
        assertTrue((long)tasks.get(-1).get(0).get("id")==task4.getId());

        assertTrue(tasks.get(0).size()==3);
        assertTrue((long)tasks.get(0).get(0).get("id")==task3.getId());
        assertTrue((long)tasks.get(0).get(1).get("id")==task1.getId());
        assertTrue((long)tasks.get(0).get(2).get("id")==task2.getId());

        assertTrue(tasks.get(1).size()==1);
        assertTrue((long)tasks.get(1).get(0).get("id")==task5.getId());
        assertTrue(((List)tasks.get(1).get(0).get("topics")).size()==1);
        assertTrue((long)((Map)((List)tasks.get(1).get(0).get("topics")).get(0)).get("id")==topicForTask5.getId());

        assertTrue(tasks.get(100).size()==1);
        assertTrue((long)tasks.get(100).get(0).get("id")==currentTask.getId());
    }

    private Task initEntChain(Date planDate){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        SpacedRepetitions spacedRepetitions = new SpacedRepetitions();
        spacedRepetitions.setTaskMapper(taskMapper);
        spacedRepDAO.save(spacedRepetitions);

        Repetition repetition = new Repetition();
        repetition.setSpacedRepetitions(spacedRepetitions);
        repetition.setPlanDate(planDate);
        repDAO.save(repetition);

        return task;
    }

}
