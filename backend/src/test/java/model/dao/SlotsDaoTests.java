package model.dao;

import model.dao.IHQuarterDAO;
import model.dao.IMeansDAO;
import model.dao.ISlotDAO;
import model.entities.HQuarter;
import model.entities.Mean;
import model.entities.Slot;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test_configs.ATestsWithTargetsMeansQuartalsGenerated;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class SlotsDaoTests extends ATestsWithTargetsMeansQuartalsGenerated {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    ISlotDAO slotDAO;


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

}
