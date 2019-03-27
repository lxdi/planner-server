package model.dto.layer;

import model.dao.IMeansDAO;
import model.dao.ISubjectDAO;
import model.dto.IMapper;
import model.dto.subject.SubjectDtoMapper;
import model.entities.Layer;
import model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class LayersDtoMapper implements IMapper<LayerDtoLazy, Layer> {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    SubjectDtoMapper subjectDtoMapper;

    @Override
    public LayerDtoLazy mapToDto(Layer layer) {
        LayerDtoLazy dto = new LayerDtoLazy();
        dto.setId(layer.getId());
        dto.setMeanid(layer.getMean().getId());
        dto.setPriority(layer.getPriority());
        dto.setDone(layer.isDone());

        if(layer.getId()>0) {
            for (Subject subject : subjectDAO.subjectsByLayer(layer)) {
                dto.getSubjects().add(subjectDtoMapper.mapToDto(subject));
            }
        }
        return dto;
    }

    @Override
    public Layer mapToEntity(LayerDtoLazy dto) {
        Layer layer = new Layer(meansDAO.meanById(dto.getMeanid()), dto.getPriority());
        layer.setId(dto.getId());
        layer.setDone(dto.isDone());
        return layer;
    }
}
