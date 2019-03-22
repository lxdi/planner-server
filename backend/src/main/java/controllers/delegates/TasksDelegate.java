package controllers.delegates;

import model.dao.*;
import model.dto.task.TaskDtoLazy;
import model.dto.task.TasksDtoMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class TasksDelegate {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    @Autowired
    IRepPlanDAO repPlanDAO;

    @Autowired
    IRepDAO repDAO;

    public TaskDtoLazy createTask(TaskDtoLazy taskDto){
        Task task = tasksDtoMapper.mapToEntity(taskDto);
        tasksDAO.saveOrUpdate(task);
        return tasksDtoMapper.mapToDto(task);
    }

    public void delete(long id){
        tasksDAO.delete(id);
    }

    public TaskDtoLazy update(TaskDtoLazy taskDto){
        Task task = tasksDtoMapper.mapToEntity(taskDto);
        tasksDAO.saveOrUpdate(task);
        return tasksDtoMapper.mapToDto(task);
    }

    public void finishTask(long taskid){
        Task task = tasksDAO.getById(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
    }

    public void finishTaskWithRepetition(long taskid, long repPlanid){
        Task task = tasksDAO.getById(taskid);
        TaskMapper taskMapper = taskMappersDAO.taskMapperForTask(task);
        finishTask(taskMapper);
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTaskMapper(taskMapper.getId());
        if(spacedRepetitions == null){
            spacedRepetitions = new SpacedRepetitions();
            spacedRepetitions.setTaskMapper(taskMapper);
            spacedRepetitions.setRepetitionPlan(repPlanDAO.getById(repPlanid));
            spacedRepDAO.save(spacedRepetitions);
            planRepetitions(spacedRepetitions);
        } else {
            //TODO clean spacedRep
        }
    }

    public void finishRepetition(long repId){
        Repetition repetition = repDAO.findOne(repId);
        repetition.setFactDate(DateUtils.currentDate());
        repDAO.save(repetition);
    }

    private void finishTask(TaskMapper taskMapper){
        if(taskMapper==null){
            throw new NullPointerException("There must be a taskMapper for the task");
        }
        taskMapper.setFinishDate(DateUtils.currentDate());
        taskMappersDAO.saveOrUpdate(taskMapper);
    }

    private List<Repetition> planRepetitions(SpacedRepetitions spacedRepetitions){
        List<Repetition> repetitions = new ArrayList<>();
        RepetitionPlan repetitionPlan = spacedRepetitions.getRepetitionPlan();
        for(int weeksToRep : repetitionPlan.getPlan()){
            Repetition repetition = new Repetition();
            repetition.setSpacedRepetitions(spacedRepetitions);
            Date planDate = DateUtils.addWeeks(spacedRepetitions.getTaskMapper().getFinishDate(), weeksToRep);
            repetition.setPlanDate(planDate);
            repDAO.save(repetition);
        }
        return repetitions;
    }

}
