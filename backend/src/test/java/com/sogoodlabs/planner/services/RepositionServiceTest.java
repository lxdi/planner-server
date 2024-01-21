package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RepositionServiceTest extends SpringTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RepositionService repositionService;

    @Autowired
    private IMeansDAO meansDAO;

    private Mean meanParent;

    private Mean meanChild1;
    private Mean meanChild2;

    private Mean meanChild1Child1;

    @BeforeEach
    public void init(){
        meanParent = createAndSaveMean("meanParent", null, null);

        meanChild2 = createAndSaveMean("meanChild2", meanParent, null);
        meanChild1 = createAndSaveMean("meanChild1", meanParent, meanChild2);

        meanChild1Child1 = createAndSaveMean("meanChild1Child1", meanChild1, null);

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();
    }

    @Test
    public void repositionMeansTest(){
        Mean meanChild1Child1_front = createMean(null, meanParent, null);
        meanChild1Child1_front.setId(meanChild1Child1.getId());

        Mean meanChild2_front = createMean(null, meanParent, meanChild1Child1);
        meanChild2_front.setId(meanChild2.getId());

        repositionService.repositionMeans(Arrays.asList(meanChild1Child1_front, meanChild2_front));

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

        meanChild1Child1 = meansDAO.findById(meanChild1Child1.getId()).get();
        assertEquals(meanChild1Child1.getParent().getId(), meanParent.getId());
        assertNull(meanChild1Child1.getNext());
        assertEquals("meanChild1Child1", meanChild1Child1.getTitle());

        meanChild2 = meansDAO.findById(meanChild2.getId()).get();
        assertEquals(meanChild2.getParent().getId(), meanParent.getId());
        assertEquals(meanChild1Child1.getId(), meanChild2.getNext().getId());
        assertEquals("meanChild2", meanChild2.getTitle());


    }


    private Mean createAndSaveMean(String title, Mean parent, Mean next){
        Mean mean = createMean(title, parent, next);
        meansDAO.save(mean);
        return mean;
    }

    private Mean createMean(String title, Mean parent, Mean next){
        Mean mean = new Mean();
        mean.setId(UUID.randomUUID().toString());
        mean.setTitle(title);
        mean.setNext(next);
        mean.setParent(parent);
        return mean;
    }

}
