package controllers.delegates;


import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.ILayerDAO;
import model.dao.ISubjectDAO;
import model.dao.ITasksDAO;
import model.entities.Subject;
import model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class SubjectsDelegate {

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    CommonMapper commonMapper;

    public List<Map<String, Object>> layersOfMean(long layerid){
        List<Map<String, Object>> result = new ArrayList<>();
        for(Subject subject : subjectDAO.subjectsByLayer(layerDAO.layerById(layerid))){
            result.add(commonMapper.mapToDto(subject));
        }
        return result;
    }

    public Map<String, Object> delete(long subjectid){
        Subject subject = subjectDAO.getById(subjectid);
        if(subject!=null) {
            List<Task> tasks = tasksDAO.tasksBySubject(subject);
            if (tasks != null && tasks.size() > 0) {
                tasks.forEach(t -> tasksDAO.delete(t.getId()));
            }
            subjectDAO.delete(subject.getId());
            return commonMapper.mapToDto(subject);
        } else {
            throw new RuntimeException("Subject doesn't exist");
        }
    }

}
