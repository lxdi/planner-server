package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
public class ForecastService {

    @Autowired
    private IRealmDAO realmDAO;

    @Autowired
    private IDayDao dayDao;

    @Autowired
    private ISlotDAO slotDAO;

    @Autowired
    private IRepDAO repDAO;

    @Autowired
    private ILayerDAO layerDAO;

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ProgressService progressService;

    public Date forecast() {
        return forecast(DateUtils.currentDate());
    }

    public Date forecast(Date fromDate) {
        var today = dayDao.findByDate(fromDate);
        var currentWeek = today.getWeek();
        var hoursPerWeek = getSlotsHours();
        var validReps = new HashSet<>(repDAO.findAllActiveAfterDate(today.getDate()));
        var tasks = getTasks();
        return forecast(currentWeek, hoursPerWeek, 0, validReps, tasks);
    }

    private Date forecast(Week currentWeek, int hoursTotal, int hoursOccupiedByTasks,
                          Set<Repetition> validReps,
                          Map<Realm, Deque<Task>> tasks) {

        var isTasksRanOut = true;

        for(var entry : tasks.entrySet()) {
            if(entry.getValue().size()>0) {
                isTasksRanOut = false;
            }
        }

        if (isTasksRanOut) {
            return null;
        }

        var repsInCurrentWeek = validReps.stream()
                .filter(rep -> rep.getPlanDay().getWeek().getId().equals(currentWeek.getId()))
                .toList();

        var hoursAvail = hoursTotal - hoursOccupiedByTasks - repsInCurrentWeek.size();

        if (hoursAvail < 2) {
            forecast(currentWeek.getNext(), hoursTotal, 0, validReps, tasks);
        }

        var mondayOfCurrentWeek = dayDao.findByWeek(currentWeek).stream()
                .filter(day -> day.getWeekDay() == DaysOfWeek.mon)
                .findFirst().get();

        Date bestDate = null;

        for(var entry : tasks.entrySet()) {

            if(entry.getValue().size()<1) {
                continue;
            }

            var curTask = entry.getValue().pop();
            var potReps = new HashSet<Repetition>();

            if (curTask.getRepetitionPlan() != null) {
                potReps.addAll(progressService.generateRepetitions(curTask.getRepetitionPlan(), mondayOfCurrentWeek.getDate(), curTask));
                validReps.addAll(potReps);

                if (!checkRepsAccommodate(currentWeek.getNext(), hoursTotal, validReps)) {
                    entry.getValue().addFirst(curTask);
                    validReps.removeAll(potReps);
                    continue;
                }
            }

            var localRes = forecast(currentWeek, hoursTotal, hoursOccupiedByTasks + 2, validReps, tasks);

            if (localRes==null) {
                localRes = mondayOfCurrentWeek.getDate();
            }

            if (bestDate == null || localRes.before(bestDate)) {
                bestDate = localRes;
            }

            entry.getValue().addFirst(curTask);
            validReps.removeAll(potReps);
        }

        return bestDate;

    }

    private boolean checkRepsAccommodate(Week currentWeek,  int hoursPerWeek, Set<Repetition> reps) {

        var lastWeek = getMostDistantWeek(reps);
        var week = currentWeek;

        while(!week.getPrev().getId().equals(lastWeek.getId())) {

            var repsTotal = repsPerWeek(week, reps);

            if (repsTotal > hoursPerWeek) {
                return false;
            }
        }

        return true;
    }

    private int repsPerWeek(Week week, Set<Repetition> reps) {
        var res = 0;

        res = res + reps.stream()
                .filter(rep -> rep.getPlanDay().getWeek().getId().equals(week.getId())).toList().size();

        return res;
    }

    private Week getMostDistantWeek(Set<Repetition> reps) {
        Repetition res = null;

        for(var rep : reps) {
            if(res == null || res.getPlanDay().getDate().before(rep.getPlanDay().getDate())) {
                res = rep;
            }
        }

        return res.getPlanDay().getWeek();

    }


    private int getSlotsHours() {
        // TODO optimize using SQL SUM on hours column
        var result = 0;
        for(var realm : realmDAO.findAll()) {
            for(var slot : slotDAO.findByRealm(realm)) {
                result = result + slot.getHours();
            }
        }
        return result;
    }

    private Map<Realm, Deque<Task>> getTasks() {
        Map<Realm, Deque<Task>> result = new HashMap<>();

        var layers = layerDAO.findWithPriority();
        layers.sort(Comparator.comparing(Layer::getPriority)); //TODO check sort

        for(var layer : layers) {
            var tasks = tasksDAO.findByLayer(layer);

            if (tasks.size() == 0){
                continue;
            }

            var realm = layer.getMean().getRealm();
            result.computeIfAbsent(realm, k -> new LinkedList<>());
            tasks.sort(Comparator.comparing(Task::getPosition)); //TODO check sort
            result.get(realm).addAll(tasks);

        }

        return result;
    }

}
