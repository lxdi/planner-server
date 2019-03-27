package model.dto.topic;

import model.dto.IMapper;
import model.entities.Task;
import model.entities.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TopicMapper implements IMapper<TopicDto, Topic> {

    @Override
    public TopicDto mapToDto(Topic entity) {
        TopicDto topicDto = new TopicDto();
        topicDto.setId(entity.getId());
        topicDto.setTaskid(entity.getTask().getId());
        topicDto.setTitle(entity.getTitle());
        topicDto.setSource(entity.getSource());
        return topicDto;
    }

    @Override
    public Topic mapToEntity(TopicDto dto) {
        throw new UnsupportedOperationException();
    }

    public Topic mapToEntity(TopicDto dto, Task task) {
        Topic topic = new Topic(dto.getTitle(), task);
        topic.setId(dto.getId());
        topic.setTitle(dto.getTitle());
        topic.setSource(dto.getSource());
        return topic;
    }
}
