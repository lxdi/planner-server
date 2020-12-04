package com.sogoodlabs.planner.controllers.dto;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.TaskMapper;

import java.util.List;

public class TaskProgressDto {

    List<TaskMapper> taskMappers;
    List<Repetition> repetitions;

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
}
