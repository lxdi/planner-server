package controllers;

import controllers.delegates.LayersDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/layer")
public class LayersRESTController {

    @Autowired
    LayersDelegate layersDelegate;

    public LayersRESTController(){}

    public LayersRESTController(LayersDelegate layersDelegate){
        this.layersDelegate = layersDelegate;
    }

    @Deprecated
    @RequestMapping(path = "/create" , method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> createLayer(@RequestBody Map<String, Object> layerDto){
        return new ResponseEntity<>(layersDelegate.createLayer(layerDto), HttpStatus.OK);
    }

    @Deprecated
    @RequestMapping(path = "/create/list" , method = RequestMethod.PUT)
    public ResponseEntity<List<Map<String, Object>>> createLayers(@RequestBody List<Map<String, Object>> layersDto){
        return new ResponseEntity<>(layersDelegate.createLayers(layersDto), HttpStatus.OK);
    }


    @RequestMapping(path = "/get/bymean/{meanid}" , method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> layersOfMean(@PathVariable("meanid") long meanid){
        return new ResponseEntity(layersDelegate.layersOfMean(meanid), HttpStatus.OK);
    }

}
