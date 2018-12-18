package model.dao;

import model.entities.SlotPosition;
import model.entities.Task;
import model.entities.TaskMapper;
import model.entities.Week;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
