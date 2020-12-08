package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.controllers.dto.TaskProgressDto;
import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.dao.ITaskMappersDAO;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ProgressService {

    @Autowired
    private ITaskMappersDAO taskMappersDAO;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private IRepPlanDAO repPlanDAO;

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

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setId(UUID.randomUUID().toString());
        taskMapper.setTask(task);
        taskMapper.setFinishDay(dayDao.findByDate(finishDate));
        taskMappersDAO.save(taskMapper);

        if(plan!=null) {
            List<Repetition> repetitions = new ArrayList<>();
            for (int step : plan.getPlan()) {
                Repetition repetition = new Repetition();
                repetition.setId(UUID.randomUUID().toString());
                repetition.setTask(task);

                Date planDate = plan.getDayStep() ? DateUtils.addDays(finishDate, step) : DateUtils.addWeeks(finishDate, step);
                Day planDay = dayDao.findByDate(planDate);
                if(planDay==null){
                    throw new RuntimeException("Day not found " + DateUtils.fromDate(planDate));
                }

                repetition.setPlanDay(planDay); // TODO what if day hasn't been created yet?
                repetitions.add(repetition);
            }
            repDAO.saveAll(repetitions);
        }
    }

    public void finishRepetition(Repetition repetition){
        if(repetition.getFactDay()!=null){
            throw new RuntimeException("Repetition is already completed " + repetition.getId());
        }

        repetition.setFactDay(dayDao.findByDate(DateUtils.currentDate()));
        repDAO.save(repetition);
    }

}
