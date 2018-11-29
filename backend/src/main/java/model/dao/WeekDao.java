package model.dao;

import model.entities.Week;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void createWeek(Week week) {
        sessionFactory.getCurrentSession().saveOrUpdate(week);
    }

    @Override
    public List<Week> allWeeks() {
        return sessionFactory.getCurrentSession().createCriteria(Week.class).list();
    }

    @Override
    public List<Week> getWeeksOfYear(int year) {
        return sessionFactory.getCurrentSession().createCriteria(Week.class).add(Restrictions.eq("year", year)).list();
    }

    @Override
    public Map<Integer, List<Week>> getWeeksMap() {
        HashMap<Integer, List<Week>> result = new HashMap<>();
        List<Week> allWeeks = this.allWeeks();
        for(Week week : allWeeks){
            List<Week> weeksForYear = result.get(week.getYear());
            if(weeksForYear==null){
                weeksForYear = new ArrayList<>(54);
                result.put(week.getYear(), weeksForYear);
            }
            weeksForYear.add(week);
        }
        return result;
    }

    @Override
    public Week weekByStartDate(int day, int month, int year) {
        return (Week) sessionFactory.getCurrentSession().createCriteria(Week.class)
                .add(Restrictions.eq("year", year))
                .add(Restrictions.eq("startDay", day))
                .add(Restrictions.eq("startMonth", month))
                .uniqueResult();
    }
}
