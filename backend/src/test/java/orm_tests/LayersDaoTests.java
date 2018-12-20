package orm_tests;

import model.dao.IHQuarterDAO;
import model.dao.ISlotDAO;
import model.entities.HQuarter;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Slot;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.ATestsWithTargetsWithMeansWithLayers;
import services.QuarterGenerator;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class LayersDaoTests extends ATestsWithTargetsWithMeansWithLayers{

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Test
    public void gettingLayersByMeanTest(){
        List<Layer> layersOfParentMean = layerDAO.getLyersOfMean(meansDao.meanByTitle(parentMeanTitle));
        List<Layer> layersOfChildMean = layerDAO.getLyersOfMean(meansDao.meanByTitle(childMeanTitle));
        List<Layer> layersOfChild2Mean = layerDAO.getLyersOfMean(meansDao.meanByTitle(child2MeanTitle));

        assertTrue(layersOfParentMean.size()==2);
        assertTrue(layersOfChildMean.size()==1);
        assertTrue(layersOfChild2Mean.size()==0);
    }

    @Test
    public void gettingLayerByPriority(){
        Layer layer = layerDAO.getLayerAtPriority(meansDao.meanByTitle(parentMeanTitle), 1);
        Layer layer2 = layerDAO.getLayerAtPriority(meansDao.meanByTitle(parentMeanTitle), 2);
        Layer layer3 = layerDAO.getLayerAtPriority(meansDao.meanByTitle(parentMeanTitle), 1000);

        assertTrue(layer.getId()!=layer2.getId());
        assertTrue(layer3==null);
    }

    @Test
    public void gettingCurrentLayerTest(){
        Mean mean = new Mean("test mean 1", realm);
        meansDao.saveOrUpdate(mean);

        Layer layer = new Layer(mean, 1);
        layerDAO.saveOrUpdate(layer);

        Layer layer2 = new Layer(mean, 2);
        layerDAO.saveOrUpdate(layer2);

        Slot slot = new Slot();
        slot.setMean(mean);
        slotDAO.saveOrUpdate(slot);

        Layer currentLayer = layerDAO.getNextLayerToSchedule(mean);

        assertTrue(currentLayer.getId()==layer2.getId());

    }

    @Test
    public void gettingCurrentLayerAsFirstTest(){
        Mean mean = new Mean("test mean 1", realm);
        meansDao.saveOrUpdate(mean);

        Layer layer = new Layer(mean, 1);
        layerDAO.saveOrUpdate(layer);

        Layer layer2 = new Layer(mean, 2);
        layerDAO.saveOrUpdate(layer2);

        Layer currentLayer = layerDAO.getNextLayerToSchedule(mean);

        assertTrue(currentLayer.getId()==layer.getId());

    }

    @Test
    public void getLayerToScheduleForSlotTest(){
        quarterGenerator.generateYear(2018);
        List<HQuarter> hQuarters = ihQuarterDAO.getHQuartersInYear(2018);

        Mean mean = new Mean("test mean", realm);
        meansDao.saveOrUpdate(mean);

        Layer layer = new Layer(mean, 1);
        layerDAO.saveOrUpdate(layer);

        Layer layer2 = new Layer(mean, 2);
        layerDAO.saveOrUpdate(layer2);

        Slot slot1 = new Slot(hQuarters.get(1), 1);
        slot1.setMean(mean);
        slotDAO.saveOrUpdate(slot1);

        Slot slot2 = new Slot(hQuarters.get(1), 2);
        slot2.setMean(mean);
        slotDAO.saveOrUpdate(slot2);

        Slot slot3 = new Slot(hQuarters.get(5), 1);
        slot2.setMean(mean);
        slotDAO.saveOrUpdate(slot3);

        assertTrue(layerDAO.getLayerToScheduleForSlot(slot1).getId()==layer.getId());
        assertTrue(layerDAO.getLayerToScheduleForSlot(slot2).getId()==layer2.getId());
        assertTrue(layerDAO.getLayerToScheduleForSlot(slot3) == null);


    }

}
