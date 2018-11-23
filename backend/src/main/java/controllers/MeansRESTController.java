package controllers;

import model.IMeansDAO;
import model.dto.mean.MeanDtoLazy;
import model.dto.mean.MeansDtoMapper;
import model.entities.Mean;
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

/**
 * Created by Alexander on 23.02.2018.
 */

@Controller
public class MeansRESTController {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    MeansDtoMapper meansDtoMapper;

    public MeansRESTController(){}

    public MeansRESTController(IMeansDAO meansDAO, MeansDtoMapper meansDtoMapper){
        this.meansDAO = meansDAO;
        this.meansDtoMapper = meansDtoMapper;
    }

    @RequestMapping(path = "/mean/all/lazy")
    public ResponseEntity<List<MeanDtoLazy>> getAllTargets(){
        List<MeanDtoLazy> result = new ArrayList<>();
        meansDAO.getAllMeans().forEach(m -> result.add(meansDtoMapper.mapToDto(m)));
        return new ResponseEntity<List<MeanDtoLazy>>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/create" , method = RequestMethod.PUT)
    public ResponseEntity<MeanDtoLazy> createTarget(@RequestBody MeanDtoLazy meanDtoLazy){
        Mean mean = meansDtoMapper.mapToEntity(meanDtoLazy);
        meansDAO.validateMean(mean);
        meansDAO.saveOrUpdate(mean);
        return new ResponseEntity<MeanDtoLazy>(meansDtoMapper.mapToDto(mean), HttpStatus.OK);
    }

    @RequestMapping(path = "/mean/delete/{meanId}" , method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("meanId") long id){
        try {
            meansDAO.deleteMean(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/mean/update" , method = RequestMethod.POST)
    public ResponseEntity<MeanDtoLazy> update(@RequestBody MeanDtoLazy meanDtoLazy){
        Mean mean = meansDtoMapper.mapToEntity(meanDtoLazy);
        meansDAO.validateMean(mean);
        meansDAO.saveOrUpdate(mean);
        return new ResponseEntity<MeanDtoLazy>(meansDtoMapper.mapToDto(mean), HttpStatus.OK);
    }

}
