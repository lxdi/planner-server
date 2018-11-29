package model.dao;

import model.entities.Layer;
import model.entities.Subject;

import java.util.List;

public interface ISubjectDAO {

    void saveOrUpdate(Subject subject);
    List<Subject> subjectsByLayer(Layer layer);
    Subject getById(long id);

}
