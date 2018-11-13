package model;

import model.entities.Mean;
import model.entities.Quartal;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuartalDao implements IQuartalDAO {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Quartal quartal) {
        sessionFactory.getCurrentSession().saveOrUpdate(quartal);
    }

    @Override
    public Quartal getById(long id) {
        return sessionFactory.getCurrentSession().get(Quartal.class, id);
    }

    @Override
    public Quartal getWithMean(Mean mean) {
        return (Quartal) sessionFactory.getCurrentSession().createCriteria(Quartal.class)
                .add(Restrictions.eq("means", mean)).uniqueResult();
    }

    @Override
    public void assignMean(Quartal quartal, Mean mean) {
        if(mean!=null && !isContainsMean(quartal, mean)){
            mean.setQuartal(quartal);
            quartal.getMeans().add(mean);
            meansDAO.saveOrUpdate(mean);
            this.saveOrUpdate(quartal);
        }
    }

    private boolean isContainsMean(Quartal quartal, Mean mean){
        for(Mean m : quartal.getMeans()){
            if(mean.getId()==m.getId() || mean == m){
                return true;
            }
        }
        return false;
    }
}
