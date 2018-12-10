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

//    @Override
//    public List<Mean> getMeansOfQuarter(HQuarter hquarter) {
//         return sessionFactory.getCurrentSession().createCriteria(Mean.class)
//                .add(Restrictions.eq("hquarter", hquarter)).list();
//    }
}
