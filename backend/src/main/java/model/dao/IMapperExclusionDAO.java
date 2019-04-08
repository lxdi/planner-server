package model.dao;

import model.entities.MapperExclusion;
import model.entities.SlotPosition;
import model.entities.Week;

import java.util.List;

public interface IMapperExclusionDAO {

    void save(MapperExclusion me);
    MapperExclusion findOne(long id);
    MapperExclusion getByWeekBySP(Week week, SlotPosition slotPosition);
    List<MapperExclusion> getByWeeksBySPs(List<Week> weeks, List<SlotPosition> slotPositions);
    void deleteBySlotPositions(List<SlotPosition> sps);
}
