package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.HQuarter;
import com.sogoodlabs.planner.model.entities.Week;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;

import static com.sogoodlabs.planner.services.DateUtils.fromDate;
import static com.sogoodlabs.planner.services.DateUtils.toDate;

/**
 * Created by Alexander on 22.04.2018.
 */

@Service
@Transactional
public class WeekDao implements IWeekDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Week getById(long id) {
        return entityManager.unwrap(Session.class).get(Week.class, id);
    }

    @Override
    public void saveOrUpdate(Week week) {
        entityManager.unwrap(Session.class).saveOrUpdate(week);
    }

    @Override
    public List<Week> allWeeks() {
        return entityManager.unwrap(Session.class).createQuery("from Week").list();
        //return entityManager.unwrap(Session.class).createCriteria(Week.class).list();
    }

    @Override
    public Week weekByStartDate(int day, int month, int year) {
        return this.weekByStartDate(toDate(year, month, day));
    }

    @Override
    public Week weekByStartDate(Date date) {
        Week week = (Week) entityManager.unwrap(Session.class).createQuery("from Week w where w.startDay = :startDay")
                .setParameter("startDay", date).uniqueResult();
        return week;
    }

    @Override
    public Week weekByYearAndNumber(int year, int number) {
        Date firstDay = toDate(year, 1, 1);
        Date lastDay = toDate(year+1, 1, 1);
        return (Week) entityManager.unwrap(Session.class)
                .createQuery("from Week w where w.startDay >= :firstDay and w.startDay < :lastDay and w.number = :number")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .setParameter("number", number)
                .uniqueResult();
//        return (Week) entityManager.unwrap(Session.class).createCriteria(Week.class)
//                .add(Restrictions.gt("startDay", firstDay))
//                .add(Restrictions.le("startDay", lastDay))
//                .add(Restrictions.eq("number", number))
//                .uniqueResult();
    }

    @Override
    public List<Week> weeksOfHquarter(HQuarter hQuarter) {
        String hql = "from Week w where w.startDay >= :hqStartWeekDay and w.startDay <= :hqEndWeekDay order by w.startDay asc";
        Query query = entityManager.unwrap(Session.class).createQuery(hql)
                .setParameter("hqStartWeekDay", hQuarter.getStartWeek().getStartDay())
                .setParameter("hqEndWeekDay", hQuarter.getEndWeek().getStartDay());
        return query.list();
    }

    @Override
    public Week weekOfDate(Date date) {
        return (Week) this.entityManager.unwrap(Session.class)
                .createQuery("from Week where startDay <= :curDate and endDay >= :curDate")
                .setParameter("curDate", date)
                .uniqueResult();
    }

    @Override
    public Week lastWeekInYear(int year) {
        Date lastYearDay = toDate(year+1, 1, 1);
        String hql = "from Week where startDay < :lastYearDay order by startDay desc";
        Query query = entityManager.unwrap(Session.class).createQuery(hql)
                .setParameter("lastYearDay", lastYearDay)
                .setMaxResults(1);
        return (Week) query.uniqueResult();
    }

}
