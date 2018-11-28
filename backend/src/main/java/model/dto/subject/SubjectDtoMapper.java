package model.dto.subject;

import model.dao.ILayerDAO;
import model.dto.IMapper;
import model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubjectDtoMapper implements IMapper<SubjectDtoLazy, Subject> {

    @Autowired
    ILayerDAO layerDAO;

    @Override
    public SubjectDtoLazy mapToDto(Subject subject) {
        SubjectDtoLazy dto =  new SubjectDtoLazy();
        dto.setId(subject.getId());
        dto.setTitle(subject.getTitle());
        dto.setPosition(subject.getPosition());
        if(subject.getLayer()!=null) {
            dto.setLayerid(subject.getLayer().getId());
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
