package model.dao;

import model.entities.HQuarter;
import model.entities.Week;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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
        return week;
    }

    @Override
    public Week weekByYearAndNumber(int year, int number) {
        Date firstDay = toDate(year, 1, 1);
        Date lastDay = toDate(year+1, 1, 1);
        return (Week) sessionFactory.getCurrentSession()
                .createQuery("from Week w where w.startDay >= :firstDay and w.startDay < :lastDay and w.number = :number")
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

    @Override
    public List<Week> weeksOfHquarter(HQuarter hQuarter) {
        String hql = "from Week w where w.startDay >= :hqStartWeekDay and w.startDay <= :hqEndWeekDay order by w.startDay asc";
        Query query = sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("hqStartWeekDay", hQuarter.getStartWeek().getStartDay())
                .setParameter("hqEndWeekDay", hQuarter.getEndWeek().getStartDay());
        return query.list();
    }

    @Override
    public Week weekOfDate(Date date) {
        return (Week) this.sessionFactory.getCurrentSession()
                .createQuery("from Week where startDay <= :curDate and endDay >= :curDate")
                .setParameter("curDate", date)
                .uniqueResult();
    }

    @Override
    public Week lastWeekInYear(int year) {
        Date lastYearDay = toDate(year+1, 1, 1);
        String hql = "from Week where startDay <= :lastYearDay order by startDay desc";
        Query query = sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("lastYearDay", lastYearDay)
                .setMaxResults(1);
        return (Week) query.uniqueResult();
    }

}
