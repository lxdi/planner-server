package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.util.DateUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskMapperDao implements ITaskMappersDAO{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public TaskMapper findOne(long id) {
        return this.entityManager.unwrap(Session.class).get(TaskMapper.class, id);
    }

    @Override
    public void saveOrUpdate(TaskMapper taskMapper) {
        entityManager.unwrap(Session.class).saveOrUpdate(taskMapper);
    }

    @Override
    public void delete(TaskMapper taskMapper) {
        this.entityManager.unwrap(Session.class).delete(taskMapper);
    }

    @Override
    public TaskMapper taskMapperForTask(Task task) {
        return (TaskMapper) entityManager.unwrap(Session.class).createQuery("from TaskMapper tm where tm.task = :task")
                .setParameter("task", task).uniqueResult();
    }

    @Override
    public TaskMapper taskMapperByWeekAndSlotPosition(Week week, SlotPosition slotPosition) {
        assert week!=null && slotPosition!=null;
        return (TaskMapper) entityManager.unwrap(Session.class).createQuery("from TaskMapper where week = :week and slotPosition = :sp")
                .setParameter("week", week)
                .setParameter("sp", slotPosition)
                .uniqueResult();
    }

    @Override
    public List<TaskMapper> taskMappersByWeeksAndSlotPositions(List<Week> weeks, List<SlotPosition> slotPositions) {
        return entityManager.unwrap(Session.class)
                .createQuery("from TaskMapper where week in :weeks and slotPosition in :sps")
                .setParameter("weeks", weeks)
                .setParameter("sps", slotPositions)
                .list();
    }

    @Override
    public Date finishDateByTaskid(long taskid) {
        return (Date) this.entityManager.unwrap(Session.class).
                createQuery("select finishDate from TaskMapper where task.id = :taskid")
                .setParameter("taskid", taskid).uniqueResult();
    }

    @Override
    public List<TaskMapper> byWeekAndDay(Week week, DaysOfWeek dayOfWeek) {
        return this.entityManager.unwrap(Session.class)
                .createQuery("from TaskMapper where week = :week and slotPosition.dayOfWeek = :dayOfWeek")
                .setParameter("week", week)
                .setParameter("dayOfWeek", dayOfWeek)
                .getResultList();
    }

    @Override
    public List<TaskMapper> taskMappersOfHqAndBefore(HQuarter hQuarter, Date date) {
        String beforeCurrentWeekQuery = "from TaskMapper where slotPosition.slot.hquarter = :hq and week.endDay<:date";
        String currentWeekQuery = "from TaskMapper where slotPosition.slot.hquarter = :hq " +
                "and week.startDay<=:date and week.endDay>=:date " +
                "and slotPosition.dayOfWeek in :daysOfWeek";

        List<TaskMapper> result = new ArrayList<>();
        result.addAll(this.entityManager.unwrap(Session.class)
                .createQuery(beforeCurrentWeekQuery)
                .setParameter("hq", hQuarter)
                .setParameter("date", date)
                .getResultList());
        List<DaysOfWeek> daysOfWeeksForCurrent = DaysOfWeek.getLessThen(DaysOfWeek.findById(DateUtils.dayOfWeek(date)));
        if (daysOfWeeksForCurrent.size() > 0) {
            result.addAll(this.entityManager.unwrap(Session.class)
                    .createQuery(currentWeekQuery)
                    .setParameter("hq", hQuarter)
                    .setParameter("daysOfWeek", daysOfWeeksForCurrent)
                    .setParameter("date", date)
                    .getResultList());
        }
        return result;
    }


}
