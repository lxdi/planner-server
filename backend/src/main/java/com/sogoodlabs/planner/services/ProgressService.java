package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dto.TaskProgressDto;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ProgressService {

    Logger log = LoggerFactory.getLogger(ProgressService.class);

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private IRepPlanDAO repPlanDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    public TaskProgressDto getByTask(Task task){
        TaskProgressDto taskProgressDto = new TaskProgressDto();

        taskProgressDto.setTaskMappers(taskMappersDAO.findByTask(task));

        List<Repetition> reps = repDAO.findByTask(task);
        reps.sort(Comparator.comparing(r -> r.getPlanDay().getDate()));
        taskProgressDto.setRepetitions(reps);

        taskProgressDto.setPlans(repPlanDAO.findAll());

        return taskProgressDto;
    }

    public void finishTask(Task task, RepetitionPlan plan, Date finishDate){

        log.info("Finishing task {}", task.getId());

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setId(UUID.randomUUID().toString());
        taskMapper.setTask(task);
        taskMapper.setFinishDay(dayDao.findByDate(finishDate));
        taskMappersDAO.save(taskMapper);

        if(plan == null){
            return;
        }

        log.info("Setting repetitions for task {}, repetition plan {}", task.getId(), plan.getId());
        repDAO.saveAll(generateRepetitions(plan, finishDate, task));
    }

    public List<Repetition> generateRepetitions(RepetitionPlan plan, Date finishDate, Task task) {
        List<Repetition> repetitions = new ArrayList<>();

        for (int step : plan.getPlan()) {
            Repetition repetition = new Repetition();
            repetition.setId(UUID.randomUUID().toString());
            repetition.setTask(task);

            Date planDate = plan.getDayStep() ? DateUtils.addDays(finishDate, step) : DateUtils.addWeeks(finishDate, step);
            Day planDay = dayDao.findByDate(planDate);

            if (planDay == null) {
                //TODO generate days and try again
                throw new RuntimeException("Day not found " + DateUtils.fromDate(planDate));
            }

            repetition.setPlanDay(planDay);
            repetitions.add(repetition);
        }

        return repetitions;
    }

    public void finishRepetition(Repetition repetition){
        if(repetition.getFactDay()!=null){
            throw new RuntimeException("Repetition is already completed " + repetition.getId());
        }

        repetition.setFactDay(dayDao.findByDate(DateUtils.currentDate()));
        repDAO.save(repetition);
        log.info("Repetition {} completed", repetition.getId());
    }

    public void removeUnfinishedReps(String taskId){
        var task = tasksDAO.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found " + taskId));
        repDAO.findAllActiveByTask(task).forEach(repetition -> repDAO.delete(repetition));
    }

}
