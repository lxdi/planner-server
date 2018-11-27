package controllers;

import model.ILayerDAO;
import model.IMeansDAO;
import model.dto.layer.LayerDtoLazy;
import model.dto.layer.LayersDtoMapper;
import model.entities.Layer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/layer")
public class LayersRESTController {

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    LayersDtoMapper mapper;

    public LayersRESTController(){}

    public LayersRESTController(ILayerDAO layerDAO, IMeansDAO meansDAO, LayersDtoMapper layersDtoMapper){
        this.layerDAO = layerDAO;
        this.meansDAO = meansDAO;
        this.mapper = layersDtoMapper;
    }

    @RequestMapping(path = "/create" , method = RequestMethod.PUT)
    public ResponseEntity<LayerDtoLazy> createLayer(@RequestBody LayerDtoLazy layerDto){
        //Layer layer = layerDAO.create(meansDAO.meanById(meanid));
        Layer layer = mapper.mapToEntity(layerDto);
        layerDAO.saveOrUpdate(layer);
        return new ResponseEntity<LayerDtoLazy>(mapper.mapToDto(layer), HttpStatus.OK);
    }

    @RequestMapping(path = "/create/list" , method = RequestMethod.PUT)
    public ResponseEntity<List<LayerDtoLazy>> createLayers(@RequestBody List<LayerDtoLazy> layersDto){
        List<LayerDtoLazy> result = new ArrayList<>();
        for(LayerDtoLazy layerDto : layersDto){
            Layer layer = mapper.mapToEntity(layerDto);
            layerDAO.saveOrUpdate(layer);
            result.add(mapper.mapToDto(layer));
        }
        //TODO return all layers of a mean, not only created ones
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(path = "/get/bymean/{meanid}" , method = RequestMethod.GET)
    public ResponseEntity<List<LayerDtoLazy>> layersOfMean(@PathVariable("meanid") long meanid){
        List<LayerDtoLazy> result = new ArrayList<>();
        for(Layer layer : layerDAO.getLyersOfMean(meansDAO.meanById(meanid))){
            result.add(mapper.mapToDto(layer));
        }
        return new ResponseEntity<List<LayerDtoLazy>>(result, HttpStatus.OK);
    }

}
