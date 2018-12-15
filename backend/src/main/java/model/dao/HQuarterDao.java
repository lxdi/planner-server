package model.dao;

import model.entities.HQuarter;
import model.entities.Week;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

import static services.DateUtils.toDate;

@Service
@Transactional
public class HQuarterDao implements IHQuarterDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(HQuarter HQuarter) {
        sessionFactory.getCurrentSession().saveOrUpdate(HQuarter);
    }

    @Override
    public HQuarter getById(long id) {
        return sessionFactory.getCurrentSession().get(HQuarter.class, id);
    }

    @Override
    public List<HQuarter> getAllHQuartals(){
        return sessionFactory.getCurrentSession()
                .createQuery("from HQuarter hq where hq.startWeek is not null")
                .list();
//        return sessionFactory.getCurrentSession().createCriteria(HQuarter.class)
//                .add(Restrictions.isNotNull("startWeek"))
//                .list();
    }

    @Override
    public List<HQuarter> getDefaultHquarters() {
        return sessionFactory.getCurrentSession()
                .createQuery("from HQuarter hq where hq.custom = false")
                .list();
//        return sessionFactory.getCurrentSession().createCriteria(HQuarter.class)
//                .add(Restrictions.eq("custom", false)).list();
    }

    @Override
    public List<HQuarter> getHQuartersInYear(int year) {
        Date firstDay = toDate(year, 1, 1);
        Date lastDay = toDate(year+1, 1, 1);
        return sessionFactory.getCurrentSession()
                .createQuery("from HQuarter where startWeek.startDay >= :firstDay and startWeek.startDay < :lastDay order by startWeek.startDay asc")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .list();
    }


    @Override
    public HQuarter getDefault() {
        HQuarter hQuarter = (HQuarter) sessionFactory.getCurrentSession()
                .createQuery("from HQuarter hq where hq.startWeek is null and hq.endWeek is null")
                .uniqueResult();
//        HQuarter hQuarter = (HQuarter) sessionFactory.getCurrentSession().createCriteria(HQuarter.class)
//                .add(Restrictions.isNull("startWeek"))
//                .add(Restrictions.isNull("endWeek")).uniqueResult();
        if(hQuarter==null){
            hQuarter = new HQuarter();
            this.saveOrUpdate(hQuarter);
        }
        return hQuarter;
    }

    @Override
    public HQuarter getHquarterWithStartingWeek(Week week) {
        return (HQuarter) sessionFactory.getCurrentSession()
                .createQuery("from HQuarter hq where hq.startWeek.startDay = :startDay")
                .setParameter("startDay", week.getStartDay())
                .uniqueResult();
    }

//    @Override
//    public List<Mean> getMeansOfQuarter(HQuarter hquarter) {
//         return sessionFactory.getCurrentSession().createCriteria(Mean.class)
//                .add(Restrictions.eq("hquarter", hquarter)).list();
//    }
}
