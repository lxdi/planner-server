package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.dto.AssignLayerDto;
import com.sogoodlabs.planner.dto.AssignMeanDto;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.dao.ITaskMappersDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.TaskMapper;
import com.sogoodlabs.planner.util.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

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

    @Before
    public void init(){
        super.init();
        weeksGenerator.generateYear(2021);
    }

    @Test
    public void scheduleSingleTest(){
        String dateString = "2021-03-11";

        Layer layer = createLayer();
        Task task1 = createTask(layer);

        AssignMeanDto assignMeanDto = new AssignMeanDto();
        assignMeanDto.setTasksPerWeek(1);
        assignMeanDto.setStartDayId(dayDao.findByDate(DateUtils.toDate(dateString)).getId());

        AssignLayerDto assignLayerDto = new AssignLayerDto();
        assignLayerDto.getTaskIds().add(task1.getId());
        assignMeanDto.getLayers().add(assignLayerDto);

        scheduleMeanService.schedule(assignMeanDto);

        super.cleanContext();

        task1 = tasksDAO.findById(task1.getId()).get();
        TaskMapper taskMapper = taskMappersDAO.findByTask(task1).get(0);
        assertEquals(dateString, DateUtils.fromDate(taskMapper.getPlanDay().getDate()));

    }

    @Test
    public void scheduleWithPlaceholdersTest(){
        String dateString = "2021-03-11";

        Layer layer = createLayer();
        Task task1 = createTask(layer);

        AssignMeanDto assignMeanDto = new AssignMeanDto();
        assignMeanDto.setTasksPerWeek(3);
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

        Layer layer = createLayer();
        Task task1 = createTask(layer);

        AssignMeanDto assignMeanDto = new AssignMeanDto();
        assignMeanDto.setTasksPerWeek(3);
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

    private Layer createLayer(){
        Layer layer = new Layer();
        layer.setId(UUID.randomUUID().toString());
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
}
