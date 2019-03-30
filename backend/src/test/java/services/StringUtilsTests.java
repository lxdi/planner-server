package services;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.entities.Subject;
import model.entities.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class StringUtilsTests {

    @Test
    public void getFullNameTest(){
        Task task = new Task();
        task.setTitle("task");

        String fullName = StringUtils.getFullName(task, new String[]{
                "subject?.layer?.mean?.realm?.title", "subject?.layer?.mean?.title",
                "subject?.layer?.priority", "subject?.title", "title"
        });

        assertTrue(fullName.equals("task"));
    }

    @Test
    public void getValueTest(){

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> someDto = new HashMap<>();
        someDto.put("id", 6564L);
        list.add(someDto);

        Map<String, Object> dtowrapper = new HashMap<>();
        dtowrapper.put("listofsomething", list);

        assertTrue((long)StringUtils.getValue(dtowrapper, "get('listofsomething').get(0).get('id')")==6564);

        Subject subject = new Subject();
        subject.setTitle("subj1");
        subject.setId(543543);

        Task task = new Task();
        task.setTitle("task1");
        task.setId(23);
        task.setSubject(subject);

        assertTrue((long)StringUtils.getValue(task, "subject.id")==543543);
    }

}
