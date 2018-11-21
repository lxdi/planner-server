package model;

import model.entities.Mean;
import model.entities.Quarter;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuarterDao implements IQuarterDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Quarter quarter) {
        sessionFactory.getCurrentSession().saveOrUpdate(quarter);
    }

    @Override
    public Quarter getById(long id) {
        return sessionFactory.getCurrentSession().get(Quarter.class, id);
    }

    @Override
    public List<Quarter> getAllQuartals(){
        return sessionFactory.getCurrentSession().createCriteria(Quarter.class).list();
    }

    @Override
    public List<Mean> getMeansOfQuarter(Quarter quarter) {
         return sessionFactory.getCurrentSession().createCriteria(Mean.class)
                .add(Restrictions.eq("quarter", quarter)).list();
    }
}
