package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
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
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    TaskMappersService taskMappersService;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    CommonMapper commonMapper;


    public MeansDelegate(){}

    public List<MeanDtoLazy> getAllMeans(){
        List<MeanDtoLazy> result = new ArrayList<>();
        meansDAO.getAllMeans().forEach(m -> result.add(meansDtoLazyMapper.mapToDto(m)));
        return result;
    }

    public MeanDtoFull create(MeanDtoFull meanDtoFull){
        if(!(meanDtoFull.getId()==0 && meanDtoFull.getNextid()==null && meanDtoFull.getRealmid()>0)){
            throw new RuntimeException("Not valid Mean to create");
        }
        Realm realm = realmDAO.realmById(meanDtoFull.getRealmid());
        Mean mean = meansDtoFullMapper.mapToEntity(meanDtoFull);
        Mean prevMean = meansDAO.getLastOfChildren(mean.getParent(), realm);
        meansDAO.validateMean(mean);
        //TODO do not save the mean if layers are not valid
        meansDAO.saveOrUpdate(mean);

        if(prevMean!=null){
            prevMean.setNext(mean);
            meansDAO.saveOrUpdate(prevMean);
        }

        saveLayers(meanDtoFull.getLayers(), mean.getId());
        return meansDtoFullMapper.mapToDto(mean);
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

    public MeanDtoLazy hideChildren(long id, boolean val){
        Mean mean = meansDAO.meanById(id);
        mean.setHideChildren(val);
        meansDAO.saveOrUpdate(mean);
        return meansDtoLazyMapper.mapToDto(mean);
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
        taskMappersService.rescheduleTaskMappers(mean, true);
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
                    Task task = tasksDtoMapper.mapToEntity(taskDto);
                    tasksDAO.saveOrUpdate(task);
                    saveTaskTestings(taskDto.getTestings(), task.getId());
                }
            }
        }
    }

    private void saveTaskTestings(List<Map<String, Object>> taskTestingsDtoList, long taskid){
        if(taskTestingsDtoList!=null && taskTestingsDtoList.size()>0){
            for(Map<String, Object> testingDto: taskTestingsDtoList){
                if(testingDto!=null) {
                    testingDto.put("taskid", taskid);
                    TaskTesting taskTesting = (TaskTesting) commonMapper.mapToEntity(testingDto, new TaskTesting());
                    if(taskTesting.getTask()==null){
                        throw new NullPointerException();
                    }
                    taskTestingDAO.save(taskTesting);
                }
            }
        }
    }

}
