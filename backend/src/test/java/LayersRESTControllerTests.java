import controllers.LayersRESTController;
import model.dto.layer.LayersDtoMapper;
import model.entities.Layer;
import model.entities.Mean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
    public void gettingLayersByMean() throws Exception {
        Mean mean = meansDao.meanByTitle(child2MeanTitle);
        Layer layer1 = new Layer(mean, 1);
        Layer layer2 = new Layer(mean, 2);
        layerDAO.saveOrUpdate(layer1);
        layerDAO.saveOrUpdate(layer2);
        assertTrue(layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle)).size()==2);

        MvcResult result = mockMvc.perform(get("/layer/get/bymean/"+mean.getId()))
                .andExpect(status().isOk()).andReturn();

        String resultStr = result.getResponse().getContentAsString();
        assertTrue(resultStr.contains("\"priority\":1"));
        assertTrue(resultStr.contains("\"priority\":2"));

    }

    @Test
    public void createNewLayerTest() throws Exception {

        assertTrue(layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle)).size()==0);

        String content = "{"+
                "\"priority\":1,"+
                "\"meanid\":"+meansDao.meanByTitle(child2MeanTitle).getId()+
                "}";
        MvcResult result = mockMvc.perform(put("/layer/create")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        String resultStr = result.getResponse().getContentAsString();

        assertTrue(layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle)).size()==1);
    }

    @Test
    public void createLayersTest() throws Exception{
        assertTrue(layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle)).size()==0);

        String content = "[{"+
                "\"priority\":1,"+
                "\"meanid\":"+meansDao.meanByTitle(child2MeanTitle).getId()+
                "},"+
                "{"+
                "\"priority\":2,"+
                "\"meanid\":"+meansDao.meanByTitle(child2MeanTitle).getId()+
                "}]";

        MvcResult result = mockMvc.perform(put("/layer/create/list")
                .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andReturn();

        String resultStr = result.getResponse().getContentAsString();

        assertTrue(layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle)).size()==2);
    }


}
