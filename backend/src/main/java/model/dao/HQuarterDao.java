package model.dao;

import model.entities.HQuarter;
import model.entities.Mean;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return sessionFactory.getCurrentSession().createCriteria(HQuarter.class).list();
    }

    @Override
    public List<HQuarter> getDefaultHquarters() {
        return sessionFactory.getCurrentSession().createCriteria(HQuarter.class)
                .add(Restrictions.eq("custom", false)).list();
    }

    @Override
    public HQuarter getByStartDate(int year, int month, int day) {
        return (HQuarter) sessionFactory.getCurrentSession().createCriteria(HQuarter.class)
                .add(Restrictions.eq("year", year))
                .add(Restrictions.eq("startMonth", month))
                .add(Restrictions.eq("startDay", day)).uniqueResult();
    }

    @Override
    public HQuarter getDefault() {
        HQuarter hQuarter = this.getByStartDate(0,0,0);
        if(hQuarter==null){
            hQuarter = new HQuarter();
            hQuarter.setYear(0);
            hQuarter.setStartMonth(0);
            hQuarter.setStartDay(0);
            this.saveOrUpdate(hQuarter);
        }
        return hQuarter;
    }

//    @Override
//    public List<Mean> getMeansOfQuarter(HQuarter hquarter) {
//         return sessionFactory.getCurrentSession().createCriteria(Mean.class)
//                .add(Restrictions.eq("hquarter", hquarter)).list();
//    }
}
