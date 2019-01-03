package controllers.delegates;

import model.dao.*;
import model.dto.layer.LayerDtoLazy;
import model.dto.layer.LayersDtoMapper;
import model.dto.mean.MeanDtoFull;
import model.dto.mean.MeanDtoLazy;
import model.dto.mean.MeansDtoFullMapper;
import model.dto.mean.MeansDtoLazyMapper;
import model.dto.subject.SubjectDtoLazy;
import model.dto.subject.SubjectDtoMapper;
import model.dto.task.TaskDtoLazy;
import model.dto.task.TasksDtoMapper;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Realm;
import model.entities.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeansDelegate {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    LayersDtoMapper layersDtoMapper;

    @Autowired
    MeansDtoLazyMapper meansDtoLazyMapper;

    @Autowired
    MeansDtoFullMapper meansDtoFullMapper;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    SubjectDtoMapper subjectDtoMapper;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    TaskMappersController taskMappersController;


    public MeansDelegate(){}

    public List<MeanDtoLazy> getAllTargets(){
        List<MeanDtoLazy> result = new ArrayList<>();
        meansDAO.getAllMeans().forEach(m -> result.add(meansDtoLazyMapper.mapToDto(m)));
        return result;
    }

    public MeanDtoLazy create(MeanDtoFull meanDtoLazy){
        if(!(meanDtoLazy.getId()==0 && meanDtoLazy.getNextid()==null && meanDtoLazy.getRealmid()>0)){
            throw new RuntimeException("Not valid Mean to create");
        }
        Realm realm = realmDAO.realmById(meanDtoLazy.getRealmid());
        Mean mean = meansDtoFullMapper.mapToEntity(meanDtoLazy);
        Mean prevMean = meansDAO.getLastOfChildren(mean.getParent(), realm);
        meansDAO.validateMean(mean);
        //TODO do not save the mean if layers are not valid
        meansDAO.saveOrUpdate(mean);

        if(prevMean!=null){
            prevMean.setNext(mean);
            meansDAO.saveOrUpdate(prevMean);
        }

        saveLayers(meanDtoLazy.getLayers(), mean.getId());
        return meansDtoLazyMapper.mapToDto(mean);
    }

    public void delete(long id){
        meansDAO.deleteMean(id);
    }

    public MeanDtoFull update(MeanDtoFull meanDtoLazy){
        return meansDtoFullMapper.mapToDto(updateMean(meanDtoLazy));
    }

    public List<MeanDtoLazy> reposition(List<MeanDtoLazy> meanDtoLazyList){
        for(MeanDtoLazy dto : meanDtoLazyList){
            Mean mean = meansDAO.meanById(dto.getId());
            mean.setNext(dto.getNextid()!=null?meansDAO.meanById(dto.getNextid()):null);
            mean.setParent(dto.getParentid()!=null?meansDAO.meanById(dto.getParentid()):null);
            meansDAO.saveOrUpdate(mean);
        }
        return meanDtoLazyList;
    }

    public MeanDtoFull getFull(long meanid){
        Mean mean = meansDAO.meanById(meanid);
        return meansDtoFullMapper.mapToDto(mean);
    }


    private Mean updateMean(MeanDtoFull meanDtoFull){
        if(!(meanDtoFull.getId()>0)){
            throw new RuntimeException("Not valid Mean Dto to update");
        }
        Mean mean = meansDtoFullMapper.mapToEntity(meanDtoFull);
        meansDAO.validateMean(mean);
        //TODO validate before saving
        meansDAO.saveOrUpdate(mean);
        saveLayers(meanDtoFull.getLayers(), mean.getId());
        taskMappersController.rescheduleTaskMappers(mean, true);
        return mean;
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
