package model.dao;

import model.entities.DaysOfWeek;
import model.entities.MapperExclusion;
import model.entities.SlotPosition;
import model.entities.Week;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import test_configs.SpringTestConfig;
import test_configs.TestCreators;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class MapperExclusionDaoTests extends SpringTestConfig {

    @Autowired
    IMapperExclusionDAO mapperExclusionDAO;

    @Autowired
    TestCreators testCreators;

    @Test
    public void getByWeekBySPTest(){
        Week week1 = testCreators.createWeek("2019-04-01", "2019-04-07");
        Week week2 = testCreators.createWeek("2019-04-08", "2019-04-14");
        Week week3 = testCreators.createWeek("2019-04-15", "2019-04-21");

        SlotPosition slotPosition1 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);
        SlotPosition slotPosition2 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);

        MapperExclusion mapperExclusion1 = testCreators.createMapperExclusion(week1, slotPosition1);
        MapperExclusion mapperExclusion2 = testCreators.createMapperExclusion(week3, slotPosition1);
        MapperExclusion mapperExclusion3 = testCreators.createMapperExclusion(week3, slotPosition2);

        assertTrue(mapperExclusionDAO.getByWeekBySP(week1, slotPosition1).getId()==mapperExclusion1.getId());
        assertTrue(mapperExclusionDAO.getByWeekBySP(week3, slotPosition1).getId()==mapperExclusion2.getId());
        assertTrue(mapperExclusionDAO.getByWeekBySP(week3, slotPosition2).getId()==mapperExclusion3.getId());
    }

    @Test
    public void getByWeeksBySPsTest(){
        Week week1 = testCreators.createWeek("2019-04-01", "2019-04-07");
        Week week2 = testCreators.createWeek("2019-04-08", "2019-04-14");
        Week week3 = testCreators.createWeek("2019-04-15", "2019-04-21");

        SlotPosition slotPosition1 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);
        SlotPosition slotPosition2 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);
        SlotPosition slotPosition3 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);

        MapperExclusion mapperExclusion1 = testCreators.createMapperExclusion(week1, slotPosition1);
        MapperExclusion mapperExclusion2 = testCreators.createMapperExclusion(week3, slotPosition1);
        MapperExclusion mapperExclusion3 = testCreators.createMapperExclusion(week3, slotPosition2);
        MapperExclusion mapperExclusion4 = testCreators.createMapperExclusion(week1, slotPosition3);

        List<MapperExclusion> result = mapperExclusionDAO.getByWeeksBySPs(Arrays.asList(week1, week2, week3),
                Arrays.asList(slotPosition1, slotPosition2));

        assertTrue(result.size()==3);
        assertTrue(result.get(0).getId()==mapperExclusion1.getId());
        assertTrue(result.get(1).getId()==mapperExclusion2.getId());
        assertTrue(result.get(2).getId()==mapperExclusion3.getId());

    }

    @Test
    public void deleteBySlopPositionsTest(){
        Week week1 = testCreators.createWeek("2019-04-01", "2019-04-07");
        Week week2 = testCreators.createWeek("2019-04-08", "2019-04-14");
        Week week3 = testCreators.createWeek("2019-04-15", "2019-04-21");

        SlotPosition slotPosition1 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);
        SlotPosition slotPosition2 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);
        SlotPosition slotPosition3 = testCreators.createSlotPosition(null, DaysOfWeek.mon, 1);

        MapperExclusion mapperExclusion1 = testCreators.createMapperExclusion(week1, slotPosition1);
        MapperExclusion mapperExclusion2 = testCreators.createMapperExclusion(week3, slotPosition1);
        MapperExclusion mapperExclusion3 = testCreators.createMapperExclusion(week3, slotPosition2);
        MapperExclusion mapperExclusion4 = testCreators.createMapperExclusion(week1, slotPosition3);

        mapperExclusionDAO.deleteBySlotPositions(Arrays.asList(slotPosition1, slotPosition2));

        //List<MapperExclusion> exclusions = mapperExclusionDAO.

        assertTrue(mapperExclusionDAO.findOne(mapperExclusion1.getId())==null);
        assertTrue(mapperExclusionDAO.findOne(mapperExclusion2.getId())==null);
        assertTrue(mapperExclusionDAO.findOne(mapperExclusion3.getId())==null);
        assertTrue(mapperExclusionDAO.findOne(mapperExclusion4.getId()).getId()==mapperExclusion4.getId());

    }

}
