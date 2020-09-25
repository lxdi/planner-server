package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IRepDAO;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.dao.ISpacedRepDAO;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import com.sogoodlabs.planner.model.entities.SpacedRepetitions;
import com.sogoodlabs.planner.model.entities.TaskMapper;
import com.sogoodlabs.planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepetitionsPlannerService {

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ISpacedRepDAO spacedRepDAO;

    @Autowired
    private IRepPlanDAO repPlanDAO;

    public SpacedRepetitions getOrCreateSpacedRepetition(TaskMapper taskMapper, long repPlanid){
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTaskMapper(taskMapper.getId());
        if(spacedRepetitions == null){
            spacedRepetitions = new SpacedRepetitions();
        }
        spacedRepetitions.setTaskMapper(taskMapper);
        spacedRepetitions.setRepetitionPlan(repPlanDAO.getOne(repPlanid));
        spacedRepDAO.save(spacedRepetitions);
        planRepetitions(spacedRepetitions);
        return spacedRepetitions;
    }

    public Repetition finishRepetition(long repId){
        Repetition repetition = repDAO.getOne(repId);
        repetition.setFactDate(DateUtils.currentDate());
        repDAO.save(repetition);
        return repetition;
    }

    /**
     * Finishes repetition and make repetitionOnly all repetitions after
     * @param repId
     */
    public void finishRepetitionWithLowing(long repId){
        Repetition repetition = finishRepetition(repId);
        repDAO.makeRepOnlyAllUnfinished(repetition.getSpacedRepetitions());
    }

    private List<Repetition> planRepetitions(SpacedRepetitions spacedRepetitions){
        RepetitionPlan repetitionPlan = spacedRepetitions.getRepetitionPlan();
        List<Repetition> repetitions =
                createRepetitions(repetitionPlan.getPlan(), spacedRepetitions.getTaskMapper().getFinishDate(), repetitionPlan.getDayStep());
        repetitions.forEach(repetition -> repetition.setSpacedRepetitions(spacedRepetitions));
        repDAO.saveAll(repetitions);
        return repetitions;
    }

    private List<Repetition> createRepetitions(int[] plan, Date finishDate, boolean isDayStep){
        List<Repetition> repetitions = new ArrayList<>();
        for(int step : plan){
            Repetition repetition = new Repetition();
            Date planDate = isDayStep? DateUtils.addDays(finishDate, step): DateUtils.addWeeks(finishDate, step);
            repetition.setPlanDate(planDate);
            repetitions.add(repetition);
        }
        return repetitions;
    }

}
