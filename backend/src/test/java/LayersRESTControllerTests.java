import controllers.LayersRESTController;
import controllers.QuartersRESTController;
import model.dto.layer.LayersDtoMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import orm_tests.conf.ATestsWithTargetsMeansQuartalsGenerated;
import orm_tests.conf.ATestsWithTargetsWithMeansWithLayers;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LayersRESTControllerTests extends ATestsWithTargetsWithMeansWithLayers {

    private MockMvc mockMvc;
    private LayersRESTController layersRESTController;

    @Autowired
    LayersDtoMapper layersDtoMapper;

    @Before
    public void init(){
        super.init();
        layersRESTController = new LayersRESTController(layerDAO, meansDao, layersDtoMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(layersRESTController).build();
    }

    @Test
    public void createNewLayerTest() throws Exception {

        assertTrue(layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle)).size()==0);

        MvcResult result = mockMvc.perform(get("/layer/create/"+meansDao.meanByTitle(child2MeanTitle).getId()))
                .andExpect(status().isOk()).andReturn();

        String resultStr = result.getResponse().getContentAsString();

        assertTrue(layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle)).size()==1);
    }


}
