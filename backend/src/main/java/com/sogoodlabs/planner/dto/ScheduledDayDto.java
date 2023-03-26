package com.sogoodlabs.planner.dto;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.planner.model.entities.Day;
import com.sogoodlabs.planner.model.entities.ExternalTask;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.TaskMapper;

import java.util.List;

public class ScheduledDayDto {

    private List<TaskMapper> taskMappers;
    private List<Repetition> repetitions;
    private List<ExternalTask> externalTasks;

    @IncludeInDto
    public List<TaskMapper> getTaskMappers() {
        return taskMappers;
    }
    public void setTaskMappers(List<TaskMapper> taskMappers) {
        this.taskMappers = taskMappers;
    }

    @IncludeInDto
    public List<Repetition> getRepetitions() {
        return repetitions;
    }
    public void setRepetitions(List<Repetition> repetitions) {
        this.repetitions = repetitions;
    }

    @IncludeInDto
    public List<ExternalTask> getExternalTasks() {
        return externalTasks;
    }
    public void setExternalTasks(List<ExternalTask> externalTasks) {
        this.externalTasks = externalTasks;
    }
}
