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
    IMeansDAO meansDAO;

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
    public Quarter getWithMean(Mean mean) {
        return (Quarter) sessionFactory.getCurrentSession().createCriteria(Quarter.class)
                .add(Restrictions.eq("means", mean)).uniqueResult();
    }


    @Override
    public void assignMean(Quarter quarter, Mean mean) {
        if(mean!=null && !isContainsMean(quarter, mean)){
            mean.setQuarter(quarter);
            quarter.getMeans().add(mean);
            meansDAO.saveOrUpdate(mean);
            this.saveOrUpdate(quarter);
        }
    }

    private boolean isContainsMean(Quarter quarter, Mean mean){
        for(Mean m : quarter.getMeans()){
            if(mean.getId()==m.getId() || mean == m){
                return true;
            }
        }
        return false;
    }
}
