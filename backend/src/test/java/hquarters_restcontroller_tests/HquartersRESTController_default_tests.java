package hquarters_restcontroller_tests;

import controllers.HquartersRESTController;
import model.dao.IHQuarterDAO;
import model.dao.IMeansDAO;
import model.dao.ISlotDAO;
import model.dao.IWeekDAO;
import model.dto.hquarter.HquarterMapper;
import model.dto.slot.SlotMapper;
import model.dto.slot.SlotPositionMapper;
import model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.AbstractTestsWithTargets;
import services.QuarterGenerator;
import services.WeeksGenerator;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static services.DateUtils.toDate;

public class HquartersRESTController_default_tests extends AbstractTestsWithTargets {

    MockMvc mockMvc;
    HquartersRESTController hquartersRESTController;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO quarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    HquarterMapper hquarterMapper;

    @Autowired
    SlotMapper slotMapper;

    @Autowired
    SlotPositionMapper slotPositionMapper;

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    WeeksGenerator weeksGenerator;

    @Autowired
    IWeekDAO weekDAO;

    @Before
    public void init(){
        super.init();
        weeksGenerator.generateYear(2018);
        hquartersRESTController = new HquartersRESTController(meansDAO, quarterDAO, slotDAO, hquarterMapper, slotMapper, slotPositionMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(hquartersRESTController).build();
    }

    @Test
    public void getSameDefaultTest(){
        HQuarter hQuarter = quarterDAO.getDefault();
        assertTrue(hQuarter.getId()==quarterDAO.getDefault().getId());
    }

    @Test
    public void setDefaultTest() throws Exception {
        HQuarter hQuarter1 = new HQuarter(weekDAO.weekByYearAndNumber(2018, 1));
        quarterDAO.saveOrUpdate(hQuarter1);

        HQuarter hQuarter2 = new HQuarter(weekDAO.weekByYearAndNumber(2018, 2));
        quarterDAO.saveOrUpdate(hQuarter2);

        HQuarter hQuarter3 = new HQuarter(weekDAO.weekByYearAndNumber(2018, 3));
        quarterDAO.saveOrUpdate(hQuarter3);

        HQuarter defaultHquarter = quarterDAO.getDefault();

        Slot slot1 = new Slot();
        slot1.setHquarter(defaultHquarter);
        slotDAO.saveOrUpdate(slot1);

        SlotPosition slotPosition1 = new SlotPosition(slot1, DaysOfWeek.sat, 1);
        slotDAO.saveOrUpdate(slotPosition1);


        String content = "{\"id\":"+defaultHquarter.getId()+","+
                "\"slots\":[{" +
                    "\"id\": "+slot1.getId()+","+
                    "\"position\":1,"+
                    "\"slotPositions\":["+
                        "{\"id\": "+slotPosition1.getId()+","+
                        "\"dayOfWeek\": \"sat\","+
                        "\"position\":1}]"
                    +"}," +
                    "{\"position\":2,"+
                    "\"slotPositions\":["+
                        "{\"dayOfWeek\": \"fri\","+
                        "\"position\":3}, "+
                        "{\"dayOfWeek\": \"mon\","+
                        "\"position\":2}]"
                    +"}]"
                +"}";

        MvcResult result = mockMvc.perform(post("/hquarter/set/default")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        assertTrue(slotDAO.getSlotsForHquarter(hQuarter1).size()==2);
        assertTrue(slotDAO.getSlotsForHquarter(hQuarter2).size()==2);
        assertTrue(slotDAO.getSlotsForHquarter(hQuarter3).size()==2);

    }


}
