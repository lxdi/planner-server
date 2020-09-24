package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Layer;
import com.sogoodlabs.planner.model.entities.Subject;

import java.util.List;

public interface ISubjectDAO {

    void saveOrUpdate(Subject subject);
    List<Subject> subjectsByLayer(Layer layer);
    Subject getById(long id);
    void delete(Subject subject);

}
