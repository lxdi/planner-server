package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.dao.ISlotDAO;
import com.sogoodlabs.planner.model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.ATestsWithTargetsMeansQuartalsGenerated;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;


@Transactional
public class SlotsDaoTests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    ILayerDAO layerDAO;


    @Test
    public void slotsAfterTest(){
        Mean mean = new Mean("test mean", realm);
        meansDAO.saveOrUpdate(mean);

        List<HQuarter> hQuarters = ihQuarterDAO.getHQuartersInYear(2018);

        Slot slot1 = new Slot(hQuarters.get(1), 1);
        slot1.setMean(mean);
        slotDAO.saveOrUpdate(slot1);

        Slot slot2 = new Slot(hQuarters.get(3), 1);
        slot2.setMean(mean);
        slotDAO.saveOrUpdate(slot2);

        Slot slot3 = new Slot(hQuarters.get(7), 1);
        slot3.setMean(mean);
        slotDAO.saveOrUpdate(slot3);

        List<Slot> slotsAfter1 = slotDAO.slotsAfter(slot1);
        assertTrue(slotsAfter1.size()==2);
        assertTrue(slotsAfter1.get(0).getId()==slot2.getId());
        assertTrue(slotsAfter1.get(1).getId()==slot3.getId());

        List<Slot> slotsAfter2 = slotDAO.slotsAfter(slot2);
        assertTrue(slotsAfter2.size()==1);
        assertTrue(slotsAfter2.get(0).getId()==slot3.getId());

        List<Slot> slotsAfter3 = slotDAO.slotsAfter(slot3);
        assertTrue(slotsAfter3.size()==0);

    }

    @Test
    public void slotsWithMeanTest(){
        Mean mean = new Mean("test mean", realm);
        meansDAO.saveOrUpdate(mean);

        List<HQuarter> hQuarters = ihQuarterDAO.getHQuartersInYear(2018);

        Slot slot1 = new Slot(hQuarters.get(1), 1);
        slot1.setMean(mean);
        slotDAO.saveOrUpdate(slot1);

        Slot slot2 = new Slot(hQuarters.get(3), 1);
        slotDAO.saveOrUpdate(slot2);

        Slot slot4 = new Slot(hQuarters.get(7), 2);
        slot4.setMean(mean);
        slotDAO.saveOrUpdate(slot4);

        Slot slot3 = new Slot(hQuarters.get(7), 1);
        slot3.setMean(mean);
        slotDAO.saveOrUpdate(slot3);

        List<Slot> slotsWithMean = slotDAO.slotsWithMean(mean);

        assertTrue(slotsWithMean.size()==3);
        assertTrue(slotsWithMean.get(0).getId()==slot1.getId());
        assertTrue(slotsWithMean.get(1).getId()==slot3.getId());
        assertTrue(slotsWithMean.get(2).getId()==slot4.getId());
    }

    @Test
    public void slotsWithLayersTest(){
        Mean mean1 = createMean("test mean1", realm);
        Layer layer11 = createLayer(mean1, 1);
        Layer layer12 = createLayer(mean1, 1);

        Mean mean2 = createMean("test mean2", realm);
        Layer layer21 = createLayer(mean2, 1);

        List<HQuarter> hQuarters = ihQuarterDAO.getHQuartersInYear(2018);

        Slot slot1 = createSlot(hQuarters.get(2), 1, mean1, layer11);
        Slot slot2 = createSlot(hQuarters.get(4), 1, mean1, layer12);
        Slot slot3 = createSlot(hQuarters.get(7), 1, mean2, layer21);
        Slot slot4 = createSlot(hQuarters.get(8), 1, null, null);

        List<Layer> layers = Arrays.asList(layer11, layer12, layer21);

        assertTrue(slotDAO.slotsWithLayers(layers).size()==3);

    }

    private Slot createSlot(HQuarter hQuarter, int position, Mean mean, Layer layer){
        Slot slot = new Slot(hQuarter, position);
        slot.setMean(mean);
        slot.setLayer(layer);
        slotDAO.saveOrUpdate(slot);
        return slot;
    }

    private Mean createMean(String title, Realm realm){
        Mean mean = new Mean(title, realm);
        meansDAO.saveOrUpdate(mean);
        return mean;
    }

    private Layer createLayer(Mean mean, int priority){
        Layer layer = new Layer(mean, priority);
        layerDAO.saveOrUpdate(layer);
        return layer;
    }

}
