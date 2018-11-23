package orm_tests;

import model.entities.Layer;
import model.entities.Mean;
import org.junit.Test;
import orm_tests.conf.ATestsWithTargetsWithMeansWithLayers;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class LayersDaoTests extends ATestsWithTargetsWithMeansWithLayers{

    @Test
    public void gettingLayersByMeanTest(){
        List<Layer> layersOfParentMean = layerDAO.getLyersOfMean(meansDao.meanByTitle(parentMeanTitle));
        List<Layer> layersOfChildMean = layerDAO.getLyersOfMean(meansDao.meanByTitle(childMeanTitle));
        List<Layer> layersOfChild2Mean = layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle));

        assertTrue(layersOfParentMean.size()==2);
        assertTrue(layersOfChildMean.size()==1);
        assertTrue(layersOfChild2Mean.size()==0);
    }

}
