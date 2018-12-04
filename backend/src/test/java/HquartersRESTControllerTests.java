import com.fasterxml.jackson.core.JsonProcessingException;
import controllers.HquartersRESTController;
import model.dao.IHQuarterDAO;
import model.dao.IMeansDAO;
import model.dao.ISlotDAO;
import model.dto.hquarter.HquarterDtoLazy;
import model.dto.hquarter.HquarterMapper;
import model.dto.slot.SlotDtoLazy;
import model.dto.slot.SlotMapper;
import model.dto.slot.SlotPositionMapper;
import model.entities.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.AbstractTestsWithTargets;
import services.QuarterGenerator;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HquartersRESTControllerTests extends AbstractTestsWithTargets {

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

    @Before
    public void init(){
        super.init();
        quarterGenerator.generateYear(2018);
        hquartersRESTController = new HquartersRESTController(meansDAO, quarterDAO, slotDAO, hquarterMapper, slotMapper, slotPositionMapper);
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

        assertTrue(slotDAO.getSlotsForHquarter(hQuarter).size()==0);

        String content = "{\"id\":"+hQuarter.getId()+","+
                "\"year\":2018,"+
                "\"startmonth\":1,"+
                "\"startday\":2,"+
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
        assertTrue(hQuarter.getStartMonth()==1);
        assertTrue(hQuarter.getStartDay()==2);
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
        slotPosition.setDaysOfWeek(DaysOfWeek.mon);
        slotPosition.setPosition(1);
        slotPosition.setSlot(slot);
        slotDAO.saveOrUpdate(slotPosition);

        MvcResult result = mockMvc.perform(post("/hquarter/assignmean/"+mean.getId()+"/toslot/"+slot.getId()))
                .andExpect(status().isOk()).andReturn();

        assertTrue(slotDAO.getById(slot.getId()).getMean()!=null);
        assertTrue(slotDAO.getById(slot.getId()).getMean().getId()==mean.getId());
        assertTrue(slotDAO.getById(slot.getId()).getMean().getTitle().equals(mean.getTitle()));

    }


}
