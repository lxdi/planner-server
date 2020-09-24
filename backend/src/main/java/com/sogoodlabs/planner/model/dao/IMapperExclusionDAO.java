package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.MapperExclusion;
import com.sogoodlabs.planner.model.entities.SlotPosition;
import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IMapperExclusionDAO extends JpaRepository<MapperExclusion, Long> {

    @Query("from MapperExclusion where week = :w and slotPosition = :sp")
    MapperExclusion getByWeekBySP(@Param("w") Week week, @Param("sp") SlotPosition slotPosition);

    @Query("from MapperExclusion where week in :weeks and slotPosition in :slotposes")
    List<MapperExclusion> getByWeeksBySPs(@Param("weeks") List<Week> weeks, @Param("slotposes") List<SlotPosition> slotPositions);

    @Modifying
    @Query("delete from MapperExclusion where slotPosition in :slotposes")
    void deleteBySlotPosition(@Param("slotposes") List<SlotPosition> sps);
}
