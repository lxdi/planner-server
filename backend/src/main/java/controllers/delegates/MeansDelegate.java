package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.*;
import model.dto.BasicDtoValidator;
import model.dto.MeansMapper;
import model.dto.TasksDtoMapper;
import model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MeansDelegate {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    TaskMappersService taskMappersService;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    BasicDtoValidator basicDtoValidator;

    @Autowired
    MeansMapper meansMapper;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    ITopicDAO topicDAO;


    public MeansDelegate(){}

    public List<Map<String, Object>> getAllMeans(){
        List<Map<String, Object>> result = new ArrayList<>();
        meansDAO.getAllMeans().forEach(m -> result.add(meansMapper.mapToDtoLazy(m)));
        return result;
    }

    public Map<String, Object> create(Map<String, Object> meanDtoFull){
        if(basicDtoValidator.checkIdPresence(meanDtoFull) || !basicDtoValidator.checkForRealm(meanDtoFull)){
            throw new RuntimeException("Not valid Mean to create");
        }
        Mean mean = meansMapper.mapToEntity(meanDtoFull);
        Mean prevMean = meansDAO.getLastOfChildren(mean.getParent(), mean.getRealm());
        //TODO do not save the mean if layers are not valid
        meansDAO.saveOrUpdate(mean);
        reassignTargetsFromParent(mean);

        Map<String, Object> result = new HashMap<>();
        if(prevMean!=null){
            prevMean.setNext(mean);
            meansDAO.saveOrUpdate(prevMean);
            result.put("previd", prevMean.getId());
        }
        if(meanDtoFull.get("layers")!=null){
            saveLayers((List<Map<String, Object>>) meanDtoFull.get("layers"), mean.getId());
        }
        return meansMapper.mapToDtoFull(mean, result);
    }

    public void delete(long id){
        meansDAO.deleteMean(id);
    }

    public Map<String, Object> update(Map<String, Object> meanDtoFull){
        if(!basicDtoValidator.checkIdPresence(meanDtoFull) || !basicDtoValidator.checkForRealm(meanDtoFull)){
            throw new RuntimeException("Not valid Mean to update");
        }
        return meansMapper.mapToDtoFull(
                                updateMean(meanDtoFull));
    }

    public List<Map<String, Object>> reposition(List<Map<String, Object>> meanDtoLazyList){
        for(Map<String, Object> dto : meanDtoLazyList){
            Mean mean = meansDAO.meanById(Long.parseLong(""+dto.get("id")));
            mean.setNext(dto.get("nextid")!=null?meansDAO.meanById(Long.parseLong(""+dto.get("nextid"))):null);
            mean.setParent(dto.get("parentid")!=null?meansDAO.meanById(Long.parseLong(""+dto.get("parentid"))):null);
            meansDAO.saveOrUpdate(mean);
        }
        return meanDtoLazyList;
    }

    public Map<String, Object> getFull(long meanid){
        Mean mean = meansDAO.meanById(meanid);
        Map<String, Object> result = meansMapper.mapToDtoFull(mean);
        return result;
    }

    public Map<String, Object> hideChildren(long id, boolean val){
        Mean mean = meansDAO.meanById(id);
        mean.setHideChildren(val);
        meansDAO.saveOrUpdate(mean);
        //TODO map with additinal
        return meansMapper.mapToDtoLazy(mean);
    }

    private void reassignTargetsFromParent(Mean mean){
        if(mean.getParent()!=null){
            List<Target> targets = mean.getParent().getTargets();
            if(targets.size()>0){
                mean.getParent().setTargets(new ArrayList<>());
                meansDAO.saveOrUpdate(mean.getParent());

                mean.getTargets().addAll(targets);
                meansDAO.saveOrUpdate(mean);
            }
        }
    }


    private Mean updateMean(Map<String, Object> meanDtoFull){
        Mean mean = meansMapper.mapToEntity(meanDtoFull);
        //TODO validate before saving
        meansDAO.saveOrUpdate(mean);
        if(meanDtoFull.get("layers")!=null){
            saveLayers((List<Map<String, Object>>) meanDtoFull.get("layers"), mean.getId());
        }
        taskMappersService.rescheduleTaskMappers(mean, true);
        return mean;
    }

    private void saveLayers(List<Map<String, Object>> layersDto, long meanId){
        if(layersDto!=null){
            for(Map<String, Object> layerDto : layersDto){
                if(layerDto!=null) {
                    layerDto.put("meanid", meanId);
                    Layer layer = commonMapper.mapToEntity(layerDto, new Layer());
                    //TODO validate before saving
                    layerDAO.saveOrUpdate(layer);
                    saveSubjects((List<Map<String, Object>>) layerDto.get("subjects"), layer.getId());
                }
            }
        }
    }

    private void saveSubjects(List<Map<String, Object>> subjectsDto, long layerid){
        if(subjectsDto!=null){
            for(Map<String, Object> subjectDto : subjectsDto){
                if(subjectDto!=null) {
                    subjectDto.put("layerid", layerid);
                    Subject subject = commonMapper.mapToEntity(subjectDto, new Subject());
                    //TODO validate before saving
                    subjectDAO.saveOrUpdate(subject);
                    saveTasks((List<Map<String, Object>>) subjectDto.get("tasks"), subject.getId());
                }
            }
        }
    }

    private void saveTasks(List<Map<String, Object>> tasksDto, long subjectid){
        if(tasksDto!=null){
            for(Map<String, Object> taskDto : tasksDto){
                if(taskDto!=null) {
                    taskDto.putIfAbsent("subjectid", subjectid);
                    Task task = tasksDtoMapper.mapToEntity(taskDto);
                    tasksDAO.saveOrUpdate(task);
                    saveTaskTopics((List<Map<String, Object>>) taskDto.get("topics"), task.getId());
                    saveTaskTestings((List<Map<String, Object>>) taskDto.get("testings"), task.getId());
                }
            }
        }
    }

    private void saveTaskTopics(List<Map<String, Object>> topicsDtos, long taskid){
        if(topicsDtos!=null && topicsDtos.size()>0){
            for(Map<String, Object> topoicDto: topicsDtos){
                if(topoicDto!=null) {
                    topoicDto.put("taskid", taskid);
                    Topic topic = commonMapper.mapToEntity(topoicDto, new Topic());
                    if(topic.getTask()==null){
                        throw new NullPointerException();
                    }
                    topicDAO.saveOrUpdate(topic);
                }
            }
        }
    }

    private void saveTaskTestings(List<Map<String, Object>> taskTestingsDtoList, long taskid){
        if(taskTestingsDtoList!=null && taskTestingsDtoList.size()>0){
            for(Map<String, Object> testingDto: taskTestingsDtoList){
                if(testingDto!=null) {
                    testingDto.put("taskid", taskid);
                    TaskTesting taskTesting = commonMapper.mapToEntity(testingDto, new TaskTesting());
                    if(taskTesting.getTask()==null){
                        throw new NullPointerException();
                    }
                    taskTestingDAO.save(taskTesting);
                }
            }
        }
    }

}
