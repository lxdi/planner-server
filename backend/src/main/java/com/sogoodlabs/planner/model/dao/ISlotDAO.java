package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Transactional
public interface ISlotDAO extends JpaRepository<Slot, Long> {

    @Query("FROM Slot slot WHERE slot.hquarter = :hquarter and slot.position = :position")
    Slot getByHquarterAndPosition(@Param("hquarter") HQuarter hQuarter, @Param("position") int position);

    @Query("FROM Slot slot WHERE slot.hquarter = :hquarter")
    List<Slot> getSlotsForHquarter(@Param("hquarter") HQuarter hquarter);

    @Query("FROM Slot slot WHERE slot.hquarter in :hquarters")
    List<Slot> getSlotsForHquarters(@Param("hquarters") List<HQuarter> hquarters);

    @Query("from Slot s where s.mean = :mean and s.hquarter.startWeek.startDay > :startDay order by s.hquarter.startWeek.startDay asc")
    List<Slot> slotsAfter(@Param("mean") Mean mean, @Param("startDay") Date startDate);

    @Query("FROM SlotPosition sp WHERE sp.slot = :slot")
    List<SlotPosition> getSlotPositionsForSlot(@Param("slot") Slot slot);

    @Query("from SlotPosition sp where sp.slot = :slot and sp.dayOfWeek = :day and sp.position = :pos")
    SlotPosition getSlotPosition(@Param("slot") Slot slot, @Param("day") DaysOfWeek daysOfWeek, @Param("pos") int position);

    @Query("from Slot s where s.mean = :mean order by s.hquarter.startWeek.startDay asc, position asc")
    List<Slot> slotsWithMean(@Param("mean") Mean mean);

    @Query("FROM Slot s WHERE s.layer in :layers")
    List<Slot> slotsWithLayers(@Param("layers") List<Layer> layers);

    List<Slot> findByLayer(Layer layer);

    List<Slot> findByMean(Mean mean);

}
