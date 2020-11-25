package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRealmDAO extends JpaRepository<Realm, String> {

}
