package com.sogoodlabs.planner.controllers.dto;

import com.sogoodlabs.common_mapper.annotations.IncludeInDto;
import com.sogoodlabs.planner.model.entities.Repetition;

import java.util.List;

public class ActualActivityDto {

    private int weekProgress;
    private int monthProgress;
    private int halfYearProgress;
    private int yearProgress;

    private List<Repetition> upcomingReps;
    private List<Repetition> aboutWeekReps;
    private List<Repetition> oneWeekLate;
    private List<Repetition> twoWeeksLate;
    private List<Repetition> memoReps;

    public int getWeekProgress() {
        return weekProgress;
    }

    public void setWeekProgress(int weekProgress) {
        this.weekProgress = weekProgress;
    }

    public int getMonthProgress() {
        return monthProgress;
    }

    public void setMonthProgress(int monthProgress) {
        this.monthProgress = monthProgress;
    }

    public int getHalfYearProgress() {
        return halfYearProgress;
    }

    public void setHalfYearProgress(int halfYearProgress) {
        this.halfYearProgress = halfYearProgress;
    }

    public int getYearProgress() {
        return yearProgress;
    }

    public void setYearProgress(int yearProgress) {
        this.yearProgress = yearProgress;
    }

    @IncludeInDto
    public List<Repetition> getUpcomingReps() {
        return upcomingReps;
    }

    public void setUpcomingReps(List<Repetition> upcomingReps) {
        this.upcomingReps = upcomingReps;
    }

    @IncludeInDto
    public List<Repetition> getAboutWeekReps() {
        return aboutWeekReps;
    }

    public void setAboutWeekReps(List<Repetition> aboutWeekReps) {
        this.aboutWeekReps = aboutWeekReps;
    }

    @IncludeInDto
    public List<Repetition> getOneWeekLate() {
        return oneWeekLate;
    }

    public void setOneWeekLate(List<Repetition> oneWeekLate) {
        this.oneWeekLate = oneWeekLate;
    }

    @IncludeInDto
    public List<Repetition> getTwoWeeksLate() {
        return twoWeeksLate;
    }

    public void setTwoWeeksLate(List<Repetition> twoWeeksLate) {
        this.twoWeeksLate = twoWeeksLate;
    }

    @IncludeInDto
    public List<Repetition> getMemoReps() {
        return memoReps;
    }

    public void setMemoReps(List<Repetition> memoReps) {
        this.memoReps = memoReps;
    }
}
