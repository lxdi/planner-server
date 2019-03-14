package model.dao;

import model.entities.RepetitionPlan;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class RepPlanDaoImpl implements IRepPlanDAO {

    @Autowired
    SessionFactory factory;

    @Override
    public void save(RepetitionPlan repetitionPlan) {
      this.factory.getCurrentSession().saveOrUpdate(repetitionPlan);
    }

    @Override
    public RepetitionPlan getById(long id) {
        return factory.getCurrentSession().get(RepetitionPlan.class, id);
    }

    @Override
    public List<RepetitionPlan> getAll() {
        return this.factory.getCurrentSession().createQuery("from RepetitionPlan").getResultList();
    }


}
