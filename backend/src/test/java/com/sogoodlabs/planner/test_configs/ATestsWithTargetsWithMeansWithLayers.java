package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.ILayerDAO;
import com.sogoodlabs.planner.model.entities.Layer;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;

abstract public class ATestsWithTargetsWithMeansWithLayers
                        extends AbstractTestsWithTargetsWithMeans{

    @Autowired
    protected ILayerDAO layerDAO;

    protected long layer1forParentMeanId;
    protected long layer2forParentMeanId;
    protected long layerForChildMeanId;

    @Override
    @Before
    public void init(){
        super.init();

        Layer layer1 = new Layer(meansDao.meanByTitle(parentMeanTitle), 1);
        Layer layer2 = new Layer(meansDao.meanByTitle(parentMeanTitle), 2);

        Layer anotherlayer = new Layer(meansDao.meanByTitle(childMeanTitle), 1);

        layerDAO.saveOrUpdate(layer1);
        layerDAO.saveOrUpdate(layer2);
        layerDAO.saveOrUpdate(anotherlayer);

        layer1forParentMeanId = layer1.getId();
        layer2forParentMeanId = layer2.getId();
        layerForChildMeanId = anotherlayer.getId();

        assertTrue(layer1.getId()>0 && layer2.getId()>0 && anotherlayer.getId()>0);
    }


}
