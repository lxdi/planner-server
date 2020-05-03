package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.MapperExclusion;
import com.sogoodlabs.planner.model.entities.SlotPosition;
import com.sogoodlabs.planner.model.entities.Week;

import java.util.List;

public interface IMapperExclusionDAO {

    void save(MapperExclusion me);
    MapperExclusion findOne(long id);
    MapperExclusion getByWeekBySP(Week week, SlotPosition slotPosition);
    List<MapperExclusion> getByWeeksBySPs(List<Week> weeks, List<SlotPosition> slotPositions);
    void deleteBySlotPositions(List<SlotPosition> sps);
}
