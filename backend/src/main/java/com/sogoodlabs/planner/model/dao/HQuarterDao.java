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

import static com.sogoodlabs.planner.services.DateUtils.toDate;


@Service
@Transactional
public class HQuarterDao implements IHQuarterDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveOrUpdate(HQuarter HQuarter) {
        entityManager.unwrap(Session.class).saveOrUpdate(HQuarter);
    }

    @Override
    public HQuarter getById(long id) {
        return entityManager.unwrap(Session.class).get(HQuarter.class, id);
    }

    @Override
    public List<HQuarter> getAllHQuartals(){
        return entityManager.unwrap(Session.class)
                .createQuery("from HQuarter hq where hq.startWeek is not null")
                .list();
    }

    @Override
    public List<HQuarter> getWorkingHquarters() {
        return entityManager.unwrap(Session.class)
                .createQuery("from HQuarter hq where hq.custom = false and startWeek is not null")
                .list();
    }

    @Override
    public List<HQuarter> getHQuartersInYear(int year) {
        Date firstDay = toDate(year, 1, 1);
        Date lastDay = toDate(year+1, 1, 1);
        return entityManager.unwrap(Session.class)
                .createQuery("from HQuarter where startWeek.startDay >= :firstDay and startWeek.startDay < :lastDay order by startWeek.startDay asc")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .list();
    }


    @Override
    public HQuarter getDefault() {
        HQuarter hQuarter = (HQuarter) entityManager.unwrap(Session.class)
                .createQuery("from HQuarter hq where hq.startWeek is null and hq.endWeek is null")
                .uniqueResult();
        if(hQuarter==null){
            hQuarter = new HQuarter();
            this.saveOrUpdate(hQuarter);
        }
        return hQuarter;
    }

    @Override
    public HQuarter getHquarterWithStartingWeek(Week week) {
        return (HQuarter) entityManager.unwrap(Session.class)
                .createQuery("from HQuarter hq where hq.startWeek.startDay = :startDay")
                .setParameter("startDay", week.getStartDay())
                .uniqueResult();
    }

    @Override
    public List<HQuarter> getPrev(long currentid, int count) {
        Date dateUntil = this.getById(currentid).getStartWeek().getStartDay().getDate();
        return entityManager.unwrap(Session.class)
                .createQuery("from HQuarter where startWeek.startDay < :dateUntil order by startWeek.startDay desc")
                .setMaxResults(count)
                .setParameter("dateUntil", dateUntil)
                .list();
    }

    @Override
    public List<HQuarter> getNext(long currentid, int count) {
        Date dateAfter = this.getById(currentid).getStartWeek().getStartDay().getDate();
        return entityManager.unwrap(Session.class)
                .createQuery("from HQuarter where startWeek.startDay > :dateAfter order by startWeek.startDay asc")
                .setMaxResults(count)
                .setParameter("dateAfter", dateAfter)
                .list();
    }

    @Override
    public HQuarter getByDate(Date date) {
        return (HQuarter) this.entityManager.unwrap(Session.class)
                .createQuery("from HQuarter where startWeek.startDay<=:date and endWeek.endDay>=:date")
                .setParameter("date", date)
                .uniqueResult();
    }

    @Override
    public HQuarter getLastInYear(int year) {
        Date lastYearDay = toDate(year+1, 1, 1);
        String hql = "from HQuarter where endWeek.startDay < :lastYearDay order by endWeek.startDay desc";
        Query query = this.entityManager.unwrap(Session.class).createQuery(hql)
                .setParameter("lastYearDay", lastYearDay)
                .setMaxResults(1);
        return (HQuarter) query.uniqueResult();
    }


}