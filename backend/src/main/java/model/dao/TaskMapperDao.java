package model.dao;

import model.entities.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.DateUtils;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskMapperDao implements ITaskMappersDAO{

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(TaskMapper taskMapper) {
        sessionFactory.getCurrentSession().saveOrUpdate(taskMapper);
    }

    @Override
    public void delete(TaskMapper taskMapper) {
        this.sessionFactory.getCurrentSession().delete(taskMapper);
    }

    @Override
    public TaskMapper taskMapperForTask(Task task) {
        return (TaskMapper) sessionFactory.getCurrentSession().createQuery("from TaskMapper tm where tm.task = :task")
                .setParameter("task", task).uniqueResult();
    }

    @Override
    public TaskMapper taskMapperByWeekAndSlotPosition(Week week, SlotPosition slotPosition) {
        assert week!=null && slotPosition!=null;
        return (TaskMapper) sessionFactory.getCurrentSession().createQuery("from TaskMapper where week = :week and slotPosition = :sp")
                .setParameter("week", week)
                .setParameter("sp", slotPosition)
                .uniqueResult();
    }

    @Override
    public List<TaskMapper> taskMappersByWeeksAndSlotPositions(List<Week> weeks, List<SlotPosition> slotPositions) {
        return sessionFactory.getCurrentSession()
                .createQuery("from TaskMapper where week in :weeks and slotPosition in :sps")
                .setParameter("weeks", weeks)
                .setParameter("sps", slotPositions)
                .list();
    }

    @Override
    public Date finishDateByTaskid(long taskid) {
        return (Date) this.sessionFactory.getCurrentSession().
                createQuery("select finishDate from TaskMapper where task.id = :taskid")
                .setParameter("taskid", taskid).uniqueResult();
    }

    @Override
    public List<TaskMapper> byWeekAndDay(Week week, DaysOfWeek dayOfWeek) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from TaskMapper where week = :week and slotPosition.dayOfWeek = :dayOfWeek")
                .setParameter("week", week)
                .setParameter("dayOfWeek", dayOfWeek)
                .getResultList();
    }

    @Override
    public List<TaskMapper> taskMappersOfHqAndBefore(HQuarter hQuarter, Date date) {
        String beforeCurrentWeekQuery = "from TaskMapper where slotPosition.slot.hquarter = :hq and week.endDay<:date";
        String currentWeekQuery = "from TaskMapper where slotPosition.slot.hquarter = :hq " +
                "and week.startDay<:date and week.endDay>:date " +
                "and slotPosition.dayOfWeek in :daysOfWeek";

        List<TaskMapper> result = new ArrayList<>();
        result.addAll(this.sessionFactory.getCurrentSession()
                .createQuery(beforeCurrentWeekQuery)
                .setParameter("hq", hQuarter)
                .setParameter("date", date)
                .getResultList());
        result.addAll(this.sessionFactory.getCurrentSession()
                .createQuery(currentWeekQuery)
                .setParameter("hq", hQuarter)
                .setParameter("daysOfWeek", DaysOfWeek.getLessThen(DaysOfWeek.findById(DateUtils.dayOfWeek(date))))
                .setParameter("date", date)
                .getResultList());

        return result;
    }


}
