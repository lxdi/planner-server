package controllers.delegates;


import model.dao.ILayerDAO;
import model.dao.ISubjectDAO;
import model.dao.ITasksDAO;
import model.dto.subject.SubjectDtoLazy;
import model.dto.subject.SubjectDtoMapper;
import model.entities.Subject;
import model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SubjectsDelegate {

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    SubjectDtoMapper subjectDtoMapper;

    @Autowired
    ITasksDAO tasksDAO;

    public List<SubjectDtoLazy> layersOfMean(long layerid){
        List<SubjectDtoLazy> result = new ArrayList<>();
        for(Subject subject : subjectDAO.subjectsByLayer(layerDAO.layerById(layerid))){
            result.add(subjectDtoMapper.mapToDto(subject));
        }
        return result;
    }

    public SubjectDtoLazy delete(long subjectid){
        Subject subject = subjectDAO.getById(subjectid);
        if(subject!=null) {
            List<Task> tasks = tasksDAO.tasksBySubject(subject);
            if (tasks != null && tasks.size() > 0) {
                tasks.forEach(t -> tasksDAO.delete(t.getId()));
            }
            subjectDAO.delete(subject.getId());
            return subjectDtoMapper.mapToDto(subject);
        } else {
            throw new RuntimeException("Subject doesn't exist");
        }
    }

}
