package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.dto.ForecastReport;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;
import com.sogoodlabs.planner.util.function.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.function.BiConsumer;

@Service
public class ForecastService {

    @Value("${forecast.type:backtracking}")
    private String forecastType;

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

    @Autowired
    private ForecastReportService forecastReportService;

    @Transactional
    public ForecastReport forecast() {
        return forecast(DateUtils.currentDate(), forecastType.equals("backtracking"));
    }

    @Transactional
    public ForecastReport forecast(Date fromDate, boolean isBacktracking) {
        var today = dayDao.findByDate(fromDate);
        var nextWeek = today.getWeek().getNext();
        var hoursPerWeek = getSlotsHours();
        var validReps = new HashSet<>(repDAO.findAllActiveAfterDate(today.getDate()));
        var tasks = getTasks();
        var report = new ForecastReport();

        if (hoursPerWeek < 2) {
            throw new RuntimeException("Not enough slots capacity " + hoursPerWeek);
        }

        Date lastTaskCompleted;

        if (isBacktracking) {
            lastTaskCompleted = forecastBacktracking(nextWeek, hoursPerWeek, 0, validReps, tasks,
                    (week, task, reps) -> forecastReportService.enrichReport(report, week, task, reps));
        } else {
            lastTaskCompleted = forecast(nextWeek, hoursPerWeek, validReps, tasks,
                    (week, task, reps) -> forecastReportService.enrichReport(report, week, task, reps));
        }

        forecastReportService.finishReport(report, lastTaskCompleted, validReps);
        return report;
    }

    private Date forecast(Week currentWeek, int hoursTotal,
                                      Set<Repetition> validReps,
                                      Map<Realm, Deque<Task>> tasks,
                                      TriConsumer<Week, Task, Set<Repetition>> taskAssignedCallback) {

        if (isTasksRanOut(tasks)) {
            return null;
        }

        var repsInCurrentWeek = validReps.stream()
                .filter(rep -> rep.getPlanDay().getWeek().getId().equals(currentWeek.getId()))
                .toList();

        var hoursAvail = hoursTotal - repsInCurrentWeek.size();

        if (hoursAvail >= 2) {

            var realmsChosen = new HashSet<Realm>();

            for (int i = 0; i < hoursAvail; i = i + 2) {
                var curTask = chooseTask(tasks, realmsChosen, validReps, hoursTotal, currentWeek);

                if (curTask == null) {
                    break;
                }

                if (taskAssignedCallback != null) {
                    taskAssignedCallback.accept(currentWeek, curTask.task, curTask.reps);
                }
            }

        }

        var res = forecast(currentWeek.getNext(), hoursTotal, validReps, tasks, taskAssignedCallback);

        if (res == null) {
            var sundayOfcurrentWeek = dayDao.findByWeek(currentWeek).stream()
                    .filter(day -> day.getWeekDay() == DaysOfWeek.sun)
                    .findFirst().get();

            return sundayOfcurrentWeek.getDate();
        }

        return res;
    }

    private Date forecastBacktracking(Week currentWeek, int hoursTotal, int hoursOccupiedByTasks,
                                      Set<Repetition> validReps,
                                      Map<Realm, Deque<Task>> tasks,
                                      TriConsumer<Week, Task, Set<Repetition>> taskAssignedCallback) {

        if (isTasksRanOut(tasks)) {
            return null;
        }

        var repsInCurrentWeek = validReps.stream()
                .filter(rep -> rep.getPlanDay().getWeek().getId().equals(currentWeek.getId()))
                .toList();

        var hoursAvail = hoursTotal - hoursOccupiedByTasks - repsInCurrentWeek.size();

        if (hoursAvail < 2) {
            return forecastBacktracking(currentWeek.getNext(), hoursTotal, 0, validReps, tasks, taskAssignedCallback);
        }

        var sundayOfCurrentWeek = dayDao.findByWeek(currentWeek).stream()
                .filter(day -> day.getWeekDay() == DaysOfWeek.sun)
                .findFirst().get();

        Date bestDate = null;
        Task chosenTask = null;
        Set<Repetition> chosenTaskReps = null;

        for(var entry : tasks.entrySet()) {

            if(entry.getValue().size()<1) {
                continue;
            }

            var curTask = entry.getValue().pop();
            var potReps = new HashSet<Repetition>();

            if (curTask.getRepetitionPlan() != null) {
                potReps.addAll(progressService.generateRepetitions(curTask.getRepetitionPlan(), sundayOfCurrentWeek.getDate(), curTask));
                validReps.addAll(potReps);

                if (!checkRepsAccommodate(currentWeek.getNext(), hoursTotal, validReps)) {
                    entry.getValue().addFirst(curTask);
                    validReps.removeAll(potReps);
                    continue;
                }
            }

            var localRes = forecastBacktracking(currentWeek, hoursTotal, hoursOccupiedByTasks + 2, validReps, tasks, taskAssignedCallback);

            if (localRes==null) {
                localRes = sundayOfCurrentWeek.getDate();
            }

            if (bestDate == null || localRes.before(bestDate)) {
                bestDate = localRes;
                chosenTask = curTask;
                chosenTaskReps = potReps;
            }

            entry.getValue().addFirst(curTask);
            validReps.removeAll(potReps);
        }

        if (taskAssignedCallback != null) {
            taskAssignedCallback.accept(currentWeek, chosenTask, chosenTaskReps);
        }

        return bestDate;

    }

    private boolean isTasksRanOut(Map<Realm, Deque<Task>> tasks) {
        var isTasksRanOut = true;

        for(var entry : tasks.entrySet()) {
            if(entry.getValue().size()>0) {
                isTasksRanOut = false;
            }
        }

        return isTasksRanOut;
    }

    static class ChosenTask {
        private Task task;
        private Set<Repetition> reps;

        public ChosenTask(Task task, Set<Repetition> reps) {
            this.task = task;
            this.reps = reps;
        }
    }

    private ChosenTask chooseTask(Map<Realm, Deque<Task>> tasks, Set<Realm> realmsChosen,
                            Set<Repetition> validReps, int hoursTotal, Week currentWeek) {

        ChosenTask res = doChooseTask(tasks, realmsChosen, validReps, hoursTotal, currentWeek);

        if(res == null) {
            realmsChosen.clear();
            res = doChooseTask(tasks, realmsChosen, validReps, hoursTotal, currentWeek);
        }

        return res;
    }

    private ChosenTask doChooseTask(Map<Realm, Deque<Task>> tasks, Set<Realm> realmsChosen,
                              Set<Repetition> validReps, int hoursTotal, Week currentWeek) {

        var wedOfCurrentWeek = dayDao.findByWeek(currentWeek).stream()
                .filter(day -> day.getWeekDay() == DaysOfWeek.wed)
                .findFirst().get();

        for(var entry : tasks.entrySet()) {

            if (entry.getValue().size() < 1) {
                continue;
            }

            if(realmsChosen != null && realmsChosen.contains(entry.getKey())) {
                continue;
            }

            var curTask = entry.getValue().pop();
            var potReps = new HashSet<Repetition>();

            if (curTask.getRepetitionPlan() != null) {
                potReps.addAll(progressService.generateRepetitions(curTask.getRepetitionPlan(), wedOfCurrentWeek.getDate(), curTask));
                validReps.addAll(potReps);

                if (!checkRepsAccommodate(currentWeek.getNext(), hoursTotal, validReps)) {
                    entry.getValue().addFirst(curTask);
                    validReps.removeAll(potReps);
                    continue;
                }
            }

            realmsChosen.add(entry.getKey());
            return new ChosenTask(curTask, potReps);
        }

        return null;
    }

    private boolean checkRepsAccommodate(Week currentWeek,  int hoursPerWeek, Set<Repetition> reps) {

        var lastWeek = getMostDistantWeek(reps);
        var week = currentWeek;

        //TODO handle week == null 'cause week couldn't have been generated
        while(!week.getPrev().getId().equals(lastWeek.getId())) {

            var repsTotal = repsPerWeek(week, reps);

            if (repsTotal > hoursPerWeek) {
                return false;
            }

            week = week.getNext();
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
            var tasks = tasksDAO.findByLayer(layer).stream()
                    .filter(t -> !t.getStatus().equals(Task.TaskStatus.COMPLETED))
                    .sorted(Comparator.comparing(Task::getPosition)).toList();

            if (tasks.size() == 0){
                continue;
            }

            var realm = layer.getMean().getRealm();
            result.computeIfAbsent(realm, k -> new LinkedList<>());
            result.get(realm).addAll(tasks);
        }

        return result;
    }

}
