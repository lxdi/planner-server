package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.ATestsWithTargetsWithMeansWithLayers;
import com.sogoodlabs.planner.services.QuarterGenerator;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

@Transactional
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
        meansDao.save(mean);

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
        meansDao.save(mean);

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

        Mean mean = createMean("test mean", realm);

        Layer layer = createLayer(mean, 1);
        Layer layer2 = createLayer(mean, 2);

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

    @Test
    public void getLyersOfMeansTest(){
        Mean mean = createMean("test mean1", realm);
        Layer layer1 = createLayer(mean, 1);
        Layer layer12 = createLayer(mean, 1);

        Mean mean2 = createMean("test mean2", realm);
        Layer layer21 = createLayer(mean, 1);

        Mean mean3 = createMean("test mean3", realm);

        List<Mean> means = Arrays.asList(mean, mean2, mean3);

        assertTrue(layerDAO.getLyersOfMeans(means).size()==3);

    }

    private Mean createMean(String title, Realm realm){
        Mean mean = new Mean(title, realm);
        meansDao.save(mean);
        return mean;
    }

    private Layer createLayer(Mean mean, int priority){
        Layer layer = new Layer(mean, priority);
        layerDAO.saveOrUpdate(layer);
        return layer;
    }

}
