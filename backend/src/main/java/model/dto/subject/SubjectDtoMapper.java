package model.dto.subject;

import model.dao.ILayerDAO;
import model.dao.ITasksDAO;
import model.dto.IMapper;
import model.dto.task.TasksDtoMapper;
import model.entities.Subject;
import model.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SubjectDtoMapper implements IMapper<SubjectDtoLazy, Subject> {

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Override
    public SubjectDtoLazy mapToDto(Subject subject) {
        SubjectDtoLazy dto =  new SubjectDtoLazy();
        dto.setId(subject.getId());
        dto.setTitle(subject.getTitle());
        dto.setPosition(subject.getPosition());
        if(subject.getLayer()!=null) {
            dto.setLayerid(subject.getLayer().getId());
        }

        if(subject.getId()>0) {
            for (Task task: tasksDAO.tasksBySubject(subject)) {
                dto.getTasks().add(tasksDtoMapper.mapToDto(task));
            }
        }

        return dto;
    }

    @Override
    public Subject mapToEntity(SubjectDtoLazy dto) {
        Subject subject = new Subject();
        subject.setId(dto.getId());
        subject.setTitle(dto.getTitle());
        subject.setPosition(dto.getPosition());
        if(dto.getLayerid()!=null){
            subject.setLayer(layerDAO.layerById(dto.getLayerid()));
        }
        return subject;
    }
}
