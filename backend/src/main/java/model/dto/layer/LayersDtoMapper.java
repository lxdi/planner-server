package model.dto.layer;

import model.IMeansDAO;
import model.dto.IMapper;
import model.entities.Layer;
import org.springframework.beans.factory.annotation.Autowired;

public class LayersDtoMapper implements IMapper<LayerDtoLazy, Layer> {

    @Autowired
    IMeansDAO meansDAO;

    @Override
    public LayerDtoLazy mapToDto(Layer layer) {
        LayerDtoLazy dto = new LayerDtoLazy();
        dto.setId(layer.getId());
        dto.setMeanid(layer.getMean().getId());
        dto.setPriority(layer.getPriority());
        return dto;
    }

    @Override
    public Layer mapToEntity(LayerDtoLazy dto) {
        Layer layer = new Layer(meansDAO.meanById(dto.getId()), dto.getPriority());
        layer.setId(dto.getId());
        return layer;
    }
}
