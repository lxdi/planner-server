package model.dao;

import model.entities.Week;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

import static services.DateUtils.fromDate;
import static services.DateUtils.toDate;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
@Transactional
public class WeekDao implements IWeekDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Week getById(long id) {
        return sessionFactory.getCurrentSession().get(Week.class, id);
    }

    @Override
    public void saveOrUpdate(Week week) {
        sessionFactory.getCurrentSession().saveOrUpdate(week);
    }

    @Override
    public List<Week> allWeeks() {
        return sessionFactory.getCurrentSession().createQuery("from Week").list();
        //return sessionFactory.getCurrentSession().createCriteria(Week.class).list();
    }

    @Override
    public Week weekByStartDate(int day, int month, int year) {
        return this.weekByStartDate(toDate(year, month, day));
    }

    @Override
    public Week weekByStartDate(Date date) {
        Week week = (Week) sessionFactory.getCurrentSession().createQuery("from Week w where w.startDay = :startDay")
                .setParameter("startDay", date).uniqueResult();
//        Week week = (Week) sessionFactory.getCurrentSession().createCriteria(Week.class)
//                .add(Restrictions.eq("startDay", date)).uniqueResult();
        if(week==null){
            throw new NullPointerException("An week with start date "+ fromDate(date) + " either doesn't exist either hasn't been generated");
        }
        return week;
    }

    @Override
    public Week weekByYearAndNumber(int year, int number) {
        Date firstDay = toDate(year, 1, 1);
        Date lastDay = toDate(year+1, 1, 1);
        return (Week) sessionFactory.getCurrentSession()
                .createQuery("from Week w where w.startDay > :firstDay and w.startDay < :lastDay and w.number = :number")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .setParameter("number", number)
                .uniqueResult();
//        return (Week) sessionFactory.getCurrentSession().createCriteria(Week.class)
//                .add(Restrictions.gt("startDay", firstDay))
//                .add(Restrictions.le("startDay", lastDay))
//                .add(Restrictions.eq("number", number))
//                .uniqueResult();
    }
}
