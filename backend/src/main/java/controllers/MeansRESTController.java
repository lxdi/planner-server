package controllers;

import model.dao.ILayerDAO;
import model.dao.IMeansDAO;
import model.dao.ISubjectDAO;
import model.dao.ITasksDAO;
import model.dto.layer.LayerDtoLazy;
import model.dto.layer.LayersDtoMapper;
import model.dto.mean.MeanDtoLazy;
import model.dto.mean.MeansDtoMapper;
import model.dto.subject.SubjectDtoLazy;
import model.dto.subject.SubjectDtoMapper;
import model.dto.task.TaskDtoLazy;
import model.dto.task.TasksDtoMapper;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Subject;
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
    ILayerDAO layerDAO;

    @Autowired
    LayersDtoMapper layersDtoMapper;

    @Autowired
    MeansDtoMapper meansDtoMapper;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    SubjectDtoMapper subjectDtoMapper;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;


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
        assert meanDtoLazy.getId()==0;
        Mean mean = meansDtoMapper.mapToEntity(meanDtoLazy);
        meansDAO.validateMean(mean);
        //TODO do not save the mean if layers are not valid
        meansDAO.saveOrUpdate(mean);
        saveLayers(meanDtoLazy.getLayers(), mean.getId());
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
        assert meanDtoLazy.getId()>0;
        Mean mean = meansDtoMapper.mapToEntity(meanDtoLazy);
        meansDAO.validateMean(mean);
        //TODO validate before saving
        meansDAO.saveOrUpdate(mean);
        saveLayers(meanDtoLazy.getLayers(), mean.getId());
        return new ResponseEntity<MeanDtoLazy>(meansDtoMapper.mapToDto(mean), HttpStatus.OK);
    }

    private void saveLayers(List<LayerDtoLazy> layersDto, long meanId){
        if(layersDto!=null){
            for(LayerDtoLazy layerDto : layersDto){
                if(layerDto!=null) {
                    layerDto.setMeanid(meanId);
                    Layer layer = layersDtoMapper.mapToEntity(layerDto);
                    //TODO validate before saving
                    layerDAO.saveOrUpdate(layer);
                    saveSubjects(layerDto.getSubjects(), layer.getId());
                }
            }
        }
    }

    private void saveSubjects(List<SubjectDtoLazy> subjectsDto, long layerid){
        if(subjectsDto!=null){
            for(SubjectDtoLazy subjectDto : subjectsDto){
                if(subjectDto!=null) {
                    subjectDto.setLayerid(layerid);
                    Subject subject = subjectDtoMapper.mapToEntity(subjectDto);
                    //TODO validate before saving
                    subjectDAO.saveOrUpdate(subject);
                    saveTasks(subjectDto.getTasks(), subject.getId());
                }
            }
        }
    }

    private void saveTasks(List<TaskDtoLazy> tasksDto, long subjectid){
        if(tasksDto!=null){
            for(TaskDtoLazy taskDto : tasksDto){
                if(taskDto!=null) {
                    taskDto.setSubjectid(subjectid);
                    tasksDAO.saveOrUpdate(tasksDtoMapper.mapToEntity(taskDto));
                }
            }
        }
    }

}
