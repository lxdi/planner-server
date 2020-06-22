package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.controllers.delegates.HquartersDelegate;
import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.AbstractTestsWithTargets;
import com.sogoodlabs.planner.services.QuarterGenerator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.sogoodlabs.planner.services.DateUtils.fromDate;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class HquartersRESTControllerTests extends AbstractTestsWithTargets {

    MockMvc mockMvc;
    HquartersRESTController hquartersRESTController;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO quarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IWeekDAO weekDAO;

    @Autowired
    HquartersDelegate hquartersDelegate;

    @Before
    public void init(){
        super.init();
        quarterGenerator.generateYear(2018);
        hquartersRESTController = new HquartersRESTController(hquartersDelegate);
        mockMvc = MockMvcBuilders.standaloneSetup(hquartersRESTController).build();
    }

    @Test
    public void getAllQuartersTest() throws Exception {
        MvcResult result =  mockMvc.perform(get("/hquarter/all")).andExpect(status().isOk()).andReturn();
        String resultStr = result.getResponse().getContentAsString();
        Assert.assertTrue(resultStr.contains("2018"));
    }

    @Test
    public void updateWithSlotsTest() throws Exception {
        HQuarter hQuarter = quarterDAO.getAllHQuartals().get(0);
        hQuarter.setStartWeek(weekDAO.weekByYearAndNumber(2018, 2));
        hQuarter.setEndWeek(weekDAO.weekByYearAndNumber(2018, 8));

        assertTrue(slotDAO.getSlotsForHquarter(hQuarter).size()==0);

        String content = "{\"id\":"+hQuarter.getId()+","+
                "\"startWeek\":{\"id\":"+hQuarter.getStartWeek().getId()+
                    ", \"startDay\":\""+fromDate(hQuarter.getStartWeek().getStartDay().getDate())+"\", \"number\": 1},"+
                "\"endWeek\":{\"id\":"+hQuarter.getEndWeek().getId()+
                ", \"startDay\":\""+fromDate(hQuarter.getEndWeek().getStartDay().getDate())+"\", \"number\": 8},"+
                "\"slots\":[{" +
                    "\"position\":1,"+
                    "\"slotPositions\":["+
                        "{\"dayOfWeek\": \"sat\","+
                        "\"position\":1},"+
                        "{\"dayOfWeek\": \"tue\","+
                        "\"position\":2}"
                        +", null]"
                    +"}, null]"
                +"}";


        entityManager.unwrap(Session.class).clear();

        MvcResult result = mockMvc.perform(post("/hquarter/update")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        hQuarter = quarterDAO.getById(hQuarter.getId());
        Slot slot = slotDAO.getSlotsForHquarter(hQuarter).get(0);
        List<SlotPosition> slotPositions = slotDAO.getSlotPositionsForSlot(slot);

        assertTrue(slotDAO.getSlotsForHquarter(hQuarter).size()==1);
        assertTrue(slot!=null);
        assertTrue(slot.getPosition()==1);
        assertTrue(slotPositions.size()==2);
        assertTrue(fromDate(hQuarter.getStartWeek().getStartDay().getDate())
                .equals(fromDate(weekDAO.weekByYearAndNumber(2018, 2).getStartDay().getDate())));

    }

    @Test
    public void assigningMeanTest() throws Exception {
        Mean mean = new Mean("test Mean", realm);
        meansDAO.saveOrUpdate(mean);

        HQuarter hQuarter = quarterDAO.getAllHQuartals().get(0);
        Slot slot =  new Slot();
        slot.setHquarter(hQuarter);
        slotDAO.saveOrUpdate(slot);

        SlotPosition slotPosition = new SlotPosition();
        slotPosition.setDayOfWeek(DaysOfWeek.mon);
        slotPosition.setPosition(1);
        slotPosition.setSlot(slot);
        slotDAO.saveOrUpdate(slotPosition);

        MvcResult result = mockMvc.perform(post("/hquarter/assignmean/"+mean.getId()+"/toslot/"+slot.getId()))
                .andExpect(status().isOk()).andReturn();

        assertTrue(slotDAO.getById(slot.getId()).getLayer().getMean()!=null);
        assertTrue(slotDAO.getById(slot.getId()).getLayer().getMean().getId()==mean.getId());
        assertTrue(slotDAO.getById(slot.getId()).getLayer().getMean().getTitle().equals(mean.getTitle()));

    }

    @Test
    public void unassigningTest() throws Exception {
        Mean mean = new Mean("test Mean", realm);
        meansDAO.saveOrUpdate(mean);

        HQuarter hQuarter = quarterDAO.getAllHQuartals().get(0);
        Slot slot =  new Slot();
        slot.setHquarter(hQuarter);
        slotDAO.saveOrUpdate(slot);

        MvcResult result = mockMvc.perform(post("/hquarter/slot/unassign/"+slot.getId()))
                .andExpect(status().isOk()).andReturn();

        //assertTrue(slot.getMean()!=null);

        slot = slotDAO.getById(slot.getId());
        assertTrue(slot.getLayer()==null);
    }


}
