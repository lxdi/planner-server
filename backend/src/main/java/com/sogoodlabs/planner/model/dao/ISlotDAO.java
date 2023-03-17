package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ISlotDAO extends JpaRepository<Slot, String> {

    @Query("select count(*) from Slot where realm = :realm")
    int findTotalByRealm(@Param("realm") Realm realm);

}
