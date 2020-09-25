package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.SlotPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface ISlotPositionDAO extends JpaRepository<SlotPosition, Long> {
}
