package model.dao;

import model.entities.SpacedRepetitions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SpacedRepDAOimpl implements ISpacedRepDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void save(SpacedRepetitions spacedRepetitions){
        this.sessionFactory.getCurrentSession().saveOrUpdate(spacedRepetitions);
    }

    @Override
    public SpacedRepetitions getSRforTaskMapper(long tmId) {
        return (SpacedRepetitions) sessionFactory.getCurrentSession()
                .createQuery("from SpacedRepetitions where taskMapper.id = :tmId")
                .setParameter("tmId", tmId)
                .uniqueResult();
    }

    @Override
    public SpacedRepetitions getSRforTask(long taskid) {
        return (SpacedRepetitions) sessionFactory.getCurrentSession()
                .createQuery("from SpacedRepetitions where taskMapper.task.id = :taskid")
                .setParameter("taskid", taskid)
                .uniqueResult();
    }
}
